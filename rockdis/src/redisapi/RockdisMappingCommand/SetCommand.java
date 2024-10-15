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

import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.string;

@Command("set")
@ParamLength(2)
public class SetCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(SetCommand.class);


    public RedisToken execute(Request request) {

        try {

            // 用來確保 Set 輸入的參數數量是正確的
            int requestLength = request.getLength();
            if (!isParameterValid(requestLength)) {
                return error("ERR wrong number of arguments for 'set' command");
            }


            if (ChannelStatusUtil.isAuthorized(request)) {

                // 遇到 flushall 正在移轉 Cache
                while (request.getServerContext().getLargeCachePoolSystem().transitCache.get()) {
//                System.out.println("遇到 flushall 正在移轉 Cache");
                    continue;
                }

                logger.debug(" Auth Status is : {}", request.getServerContext().getAuthStatus());
                SafeString timestamp = request.getParam(0);
                SafeString key = request.getParam(1);
                SafeString value = request.getParam(2);
                logger.debug("Info => Timestamp:{} ,Key: {} , Value: {}", timestamp, key, value);


                if (key.toString().equals("tse") || key.toString().equals("otc")) {
                    System.out.println("寫入 tse or otc 資訊, " + key.toString() + " ," + value.toString());
                }

                // 實際開始進行寫入
                // Cache Mechanism
                if (request.getServerContext().getCacheSystemStatus()) {
                    logger.debug("Enabled Cache System.");

                    if (!request.getServerContext().getSafemode()) {
                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        fieldValueMap.put("setCommandNoFiled", value.toString());
                        fieldValueMap.put("rockscommand", "set");
                        request.getServerContext().getLargeCachePoolSystem().putToLCPS(key.toString(), fieldValueMap);
                    } else {
                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        fieldValueMap.put("setCommandNoFiled", value.toString());
                        fieldValueMap.put("rockscommand", "set");
                        request.getServerContext().getLargeCachePoolSystem().putToWaitingCache(key.toString(), fieldValueMap);
                    }
                } else {

                    if (!request.getServerContext().getSafemode()) {
                        logger.debug("Disabled Cache System.");
                        Rockdis rockdisInstance = request.getServerContext().getRockdisInstance();

                        // 判斷 rocksdis instance 是否為 null
                        if (rockdisInstance == null) {
                            logger.error("Rockdis Instance is null, Please check directory setting.");
                        }

                        try {
                            rockdisInstance.set(key.toString(), value.toString());
                        } catch (RocksDBException e) {
                            e.printStackTrace();
                        }
                    } else {
                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        fieldValueMap.put("setCommandNoFiled", value.toString());
                        fieldValueMap.put("rockscommand", "set");
                        request.getServerContext().getLargeCachePoolSystem().putToWaitingCache(key.toString(), fieldValueMap);
                    }


                }

                return string("OK");
            } else {
                return error("Auth Failed!");
            }
        } catch (Exception e) {
            // 避免 process 因為 exception而被卡住，
            // 因為前端需要接收一個 resp return，所以遇到exception 就直接回傳 error
            logger.error(" 執行失敗，Request => {} \n 詳細 stack traced => ", request, e);
            return error("執行失敗，請查看 error log");
        }
    }

    private boolean isParameterValid(int requestLength) {
        return requestLength == 3;
    }
}
