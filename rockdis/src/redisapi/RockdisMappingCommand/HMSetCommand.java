package redisapi.RockdisMappingCommand;

import rocksdbapi.Rockdis.Rockdis;
import static rocksdbapi.Rockdis.RockdisProtocol.combineDBKeyWithKeyField;

import redisapi.Util.ChannelStatusUtil;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static com.github.tonivade.resp.protocol.RedisToken.*;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/9/24
 */

@Command("hmset")
public class HMSetCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(HMSetCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {
            // 用來確保 HMSet 輸入的參數數量是正確的
            int requestLength = request.getLength();
            if (!isParameterValid(requestLength)) {
                return error("ERR wrong number of arguments for 'hmset' command");
            }

            // 用來判斷 request有沒有輸入正確的 password， password 設定在 rockdis.properties
            if (ChannelStatusUtil.isAuthorized(request)) {
                SafeString key = request.getParam(1);

                logger.debug(" [HMSET Command triggered] Cache System Status : " + request.getServerContext().getCacheSystemStatus());

                // 遇到 flushall 正在移轉 Cache
                while (request.getServerContext().getLargeCachePoolSystem().transitCache.get()) {
//                System.out.println("遇到 flushall 正在移轉 Cache");
                    continue;
                }

                Map<String, String> hmsetMap = new HashMap<>();
                // 實際開始進行寫入
                if (request.getServerContext().getCacheSystemStatus()) {
                    // 有啟動 Cache System 則進行寫入
                    logger.debug("Enable Cache System.");

                    // todo 這裡用來debug用
                    if (key.toString().equals("stockquote_20231123_otc_62574.tw_20231123")) {
                        System.out.print("[HMSetCommand] HMSet Key : " + request.getParam(1).toString() + " ");
                        for (int i = 2; i < requestLength - 1; i += 2) {
                            System.out.print("Field : " + request.getParam(i).toString() + " , Value : " + request.getParam(i + 1).toString() + " ");
                        }
                        System.out.println("=====");
                        System.out.println();
                    }

                    if (!request.getServerContext().getSafemode()) {
                        // 針對多個 field 與 value 組合 寫入 Cache
                        NavigableMap<String, String> filedValueMap = new ConcurrentSkipListMap<>();
                        for (int i = 2; i < requestLength - 1; i += 2) {
                            if (request.toString().contains("2330")) {
                                logger.debug("Request key : {}, Request field : {}, request value : {}",
                                        request.getParam(1).toString(),
                                        request.getParam(i).toString(),
                                        request.getParam(i + 1).toString());
                            }
                            filedValueMap.put(request.getParam(i).toString(), request.getParam(i + 1).toString());
                        }
                        filedValueMap.put("rockscommand", "hmset");
                        request.getServerContext().getLargeCachePoolSystem().putToLCPS(request.getParam(1).toString(), filedValueMap);
                    } else {
                        // 針對多個 field 與 value 組合 寫入 Cache
                        NavigableMap<String, String> filedValueMap = new ConcurrentSkipListMap<>();
                        for (int i = 2; i < requestLength - 1; i += 2) {
                            if (request.toString().contains("2330")) {
                                logger.debug("Request key : {}, Request field : {}, request value : {}",
                                        request.getParam(1).toString(),
                                        request.getParam(i).toString(),
                                        request.getParam(i + 1).toString());
                            }
                            filedValueMap.put(request.getParam(i).toString(), request.getParam(i + 1).toString());
                        }
                        filedValueMap.put("rockscommand", "hmset");
                        request.getServerContext().getLargeCachePoolSystem().putToWaitingCache(request.getParam(1).toString(), filedValueMap);
                    }
                } else {

                    if (!request.getServerContext().getSafemode()) {
                        // 沒有啟動 Cache System 則直接寫入 RocksDB
                        logger.debug("Disable Cache System.");
                        for (int i = 2; i < requestLength - 1; i += 2) {

                            if (request.toString().contains("stockquote_20231123_tse_2330.tw_20231123")) {
                                System.out.print("Field : " + request.getParam(i).toString() + " , Value : " + request.getParam(i + 1).toString() + " ");
                            }

                            hmsetMap.put(request.getParam(i).toString(), request.getParam(i + 1).toString());
                        }

                        Rockdis rockdisInstance = request.getServerContext().getRockdisInstance();
                        try {
                            rockdisInstance.hmset(key.toString(), hmsetMap);
                        } catch (RocksDBException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 針對多個 field 與 value 組合 寫入 Cache
                        NavigableMap<String, String> filedValueMap = new ConcurrentSkipListMap<>();
                        for (int i = 2; i < requestLength - 1; i += 2) {
                            if (request.toString().contains("2330")) {
                                logger.debug("Request key : {}, Request field : {}, request value : {}",
                                        request.getParam(1).toString(),
                                        request.getParam(i).toString(),
                                        request.getParam(i + 1).toString());
                            }
                            filedValueMap.put(request.getParam(i).toString(), request.getParam(i + 1).toString());
                        }
                        filedValueMap.put("rockscommand", "hmset");
                        request.getServerContext().getLargeCachePoolSystem().putToWaitingCache(request.getParam(1).toString(), filedValueMap);
                    }
                }

                return responseOk();

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
        // hmset 的參數撇除 timestamp 與 key 之後，必須是成對的
        return requestLength > 2 && (requestLength - 2) % 2 == 0;
    }
}
