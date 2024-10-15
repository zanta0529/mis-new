package redisapi.RockdisMappingCommand;


import rocksdbapi.Rockdis.Rockdis;
import redisapi.Util.ChannelStatusUtil;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.string;

@Command("get")
@ParamLength(1)
public class GETCommand implements RespCommand {

    private static final Logger logger = LoggerFactory.getLogger(GETCommand.class);

    public RedisToken execute(Request request) {

        try {
            //確保 Get 參數是正確的
            int requestLength = request.getLength();
            if (!isParameterValid(requestLength)) {
                return error("ERR wrong number of arguments for 'get' command");
            }

            if (ChannelStatusUtil.isAuthorized(request)) {
                SafeString key = request.getParam(1);
                String result = "";

                // 如果在切換DB，get相關的都回 null
                if (request.getServerContext().getSafemode() == true) {
                    return string("null");
                }

                // 確認是否有啟動 Cache System
                if (request.getServerContext().getCacheSystemStatus()) {

                    Object LCPSResult = request.getServerContext().getLargeCachePoolSystem().getFromLCPS(key.toString());
                    // 先判斷從 LCPS拿到的資料是不是 null
                    String value = null;
                    if (LCPSResult != null && LCPSResult instanceof Map<?, ?>) {
                        logger.debug("Get data from LCPS.");
                        value = ((Map<String, String>) LCPSResult).get("setCommandNoFiled");
                    }

                    if (value != null) {
                        result = value;
                    } else {
//                    System.out.println("Not Hit In LCPS Cache, The get key is : " + key);
//                    logger.debug("Miss from LCPS, go to RocksDB to retrieve data.");
                        Rockdis rockdisInstance = request.getServerContext().getSecondRockdisInstance();
                        try {
                            result = rockdisInstance.get(key.toString());
                            // result 不是 null 則把從RocksDB取得的資料寫到LCPS
                            if (!result.equals("null")) {
                                logger.debug("回寫資料到 LCPS.");
                                NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                                fieldValueMap.put("setCommandNoFiled", result);
                                request.getServerContext().getLargeCachePoolSystem().putToLCPS(key.toString(), fieldValueMap);
                            } else {
                                logger.debug("Even RocksDB has no data of this key : {}.", key);
                            }
                        } catch (RocksDBException e) {
                            e.printStackTrace();
                        }
                    }
//                }

                } else {
                    // 沒有啟動 Cache System 直接跟 RocksDB 拿資料
                    Rockdis rockdisInstance = request.getServerContext().getSecondRockdisInstance();
                    try {
                        result = rockdisInstance.get(key.toString());
                    } catch (RocksDBException e) {
                        e.printStackTrace();
                    }
                }


                logger.debug("Data info -> key : {}, value : {}", key, result);
                return string(result);
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
        return requestLength == 2;
    }
}
