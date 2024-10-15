package redisapi.RockdisMappingCommand;

import rocksdbapi.Rockdis.Rockdis;
import redisapi.Util.ChannelStatusUtil;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import static rocksdbapi.Rockdis.RockdisProtocol.combineDBKeyWithKeyField;
import static com.github.tonivade.resp.protocol.RedisToken.*;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/9/25
 */

@Command("hmget")
public class HMGetCommand implements RespCommand {
    private static Logger logger = LoggerFactory.getLogger(HMGetCommand.class);


    @Override
    public RedisToken execute(Request request) {

        try {
            int requestLength = request.getLength();
            if (!isParameterValid(requestLength)) {
                return error("ERR wrong number of arguments for 'hmget' command");
            }

            if (ChannelStatusUtil.isAuthorized(request)) {
                SafeString key = request.getParam(1);

                logger.debug(" [HMGET Command triggered] Cache System Status : " + request.getServerContext().getCacheSystemStatus());

                // 如果在切換DB，get相關的都回 null
                if (request.getServerContext().getSafemode() == true) {
                    return array(new ArrayList<>());
                }

                List<String> hmgetReturnList = new LinkedList<>();
                String[] hmgetFieldArray = new String[requestLength - 2];
//            SafeString[] hmgetFieldArraySafeString = new SafeString[requestLength - 2];
                for (int i = 0; i < requestLength - 2; i++) {
                    hmgetFieldArray[i] = request.getParam(i + 2).toString();
//                hmgetFieldArraySafeString[i] = request.getParam(i + 2);
                }


                // 確認是否有啟動 Cache System
                if (request.getServerContext().getCacheSystemStatus()) {

                    // 先問 LCPS 是否有值
                    Object LCPSResult = request.getServerContext().getLargeCachePoolSystem().getFromLCPS(key.toString());
                    if (LCPSResult != null && LCPSResult instanceof Map<?, ?>) {
                        logger.debug("Get data from LCPS.");

                        for (String eachField : hmgetFieldArray) {
                            if (((Map<String, String>) LCPSResult).get(eachField) != null) {
                                hmgetReturnList.add(((Map<String, String>) LCPSResult).get(eachField));
                            } else {
                                hmgetReturnList.add("null");
                            }
                        }
                    } else {
                        // LCPS沒有值，所以去後端拿值
                        logger.debug("Miss from LCPS, go to RocksDB to retrieve data.");
                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        Map<String, String> hgetallMap = request.getServerContext().getLargeCachePoolSystem().putToLCPS(key.toString(), fieldValueMap);
                        for (String eachField : hmgetFieldArray) {
                            if (hgetallMap.get(eachField) != null) {
                                hmgetReturnList.add(hgetallMap.get(eachField));
                            } else {
                                hmgetReturnList.add("null");
                            }
                        }


//                    try {
//                        Rockdis rockdisInstance = request.getServerContext().getRockdisInstance();
//                        hmgetReturnList = rockdisInstance.hmget(key.toString(), hmgetFieldArray);
//                        // hmget 從 rocks拿回來是 List Array，要轉成NavigableMap，兩個 list的元素為一個 navigableMap的entry
//                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
//                        for (int i = 0; i < hmgetReturnList.size(); i++) {
//                            fieldValueMap.put(hmgetFieldArray[i], hmgetReturnList.get(i));
//                        }
//                        request.getServerContext().getLargeCachePoolSystem().putToLCPS(key.toString(), fieldValueMap);
//
//                    } catch (RocksDBException e) {
//                        e.printStackTrace();
//                    }
                    }
//                // 走訪 hmgetFieldArray
//                for (String eachField : hmgetFieldArray) {
//                    //詢問LCPS是否有值
//                    byte[] combinedKey = combineDBKeyWithKeyField(key.toString(),eachField);
////                    String LCPSResult = request.getServerContext().getLargeCachePoolSystem().getFromLCPS(new String(combinedKey));
//                    String LCPSResult = "還沒改善";
//                    if (LCPSResult != null) {
//                        logger.debug("Get data from LCPS.");
//                        hmgetReturnList.add(LCPSResult);
//                    } else {
//                        // LCPS沒有值，所以去後端拿值
//                        logger.debug("Miss from LCPS, go to RocksDB to retrieve data.");
//                        try {
//                            hmgetReturnList.add(rockdisInstance.hget(key.toString(), eachField));
//                        } catch (RocksDBException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
                } else {
                    // 沒有啟動 Cache System，直接跟 RocksDB取值
                    logger.debug("Do not enable cache system. Directly get data from RocksDB.");
                    try {
                        Rockdis rockdisInstance = request.getServerContext().getSecondRockdisInstance();
                        hmgetReturnList = rockdisInstance.hmget(key.toString(), hmgetFieldArray);
                    } catch (RocksDBException e) {
                        e.printStackTrace();
                    }
                }

                // TODO 由於一開始並沒有串接好 redis server與redis client。所以必須從redis server 取得的資訊在進行 re-process
                List<RedisToken> returnRedisToken = new ArrayList<>();
                for (int i = 0; i < hmgetReturnList.size(); i++) {
                    returnRedisToken.add(string(hmgetReturnList.get(i)));
                }

                return array(returnRedisToken);
            } else {
                return error("Auth Failed.");
            }
        } catch (Exception e) {
            // 避免 process 因為 exception而被卡住，
            // 因為前端需要接收一個 resp return，所以遇到exception 就直接回傳 error
            logger.error(" 執行失敗，Request => {} \n 詳細 stack traced => ", request, e);
            return error("執行失敗，請查看 error log");
        }
    }

    private boolean isParameterValid(int requestLength) {
        return requestLength > 2;
    }
}
