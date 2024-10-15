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

import static rocksdbapi.Rockdis.RockdisProtocol.combineDBKeyWithKeyField;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.string;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/9/24
 */

@Command("hget")
@ParamLength(2)
public class HGetCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(HGetCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {
            int requestLength = request.getLength();
            if (!isParameterValid(requestLength)) {
                return error("ERR wrong number of arguments for 'hget' command");
            }


            if (ChannelStatusUtil.isAuthorized(request)) {
                SafeString key = request.getParam(1);
                SafeString field = request.getParam(2);
                String result = "";

                logger.debug(" [HSET Command triggered] Cache System Status : " + request.getServerContext().getCacheSystemStatus());

                // 如果在切換DB，get相關的都回 null
                if (request.getServerContext().getSafemode() == true) {
                    return string("null");
                }

                // 確認是否有啟動 Cache System
                if (request.getServerContext().getCacheSystemStatus()) {
                    // 詢問 LCPS 是否有值
                    Object LCPSResult = request.getServerContext().getLargeCachePoolSystem().getFromLCPS(key.toString());
                    // 先嘗試把 LCPSResult 解析出來
                    String value = null;
                    if (LCPSResult != null && LCPSResult instanceof Map<?, ?>) {
                        logger.debug("Get data from LCPS Cache.");
                        value = ((Map<String, String>) LCPSResult).get(field.toString());
                    }

                    if (value != null) {
                        // Cache Hit 回傳 value的結果
                        result = value;
                    } else {
                        logger.debug("Miss from LCPS, go to RocksDB to retrieve data.");
                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        Map<String, String> returnMap = request.getServerContext().getLargeCachePoolSystem().putToLCPS(key.toString(), fieldValueMap);

                        if (returnMap != null) {
                            result = returnMap.getOrDefault(field.toString(), "null");
                        } else {
                            result = "null";
                        }
                    }
                } else {

                    // 沒有啟動 Cache System 直接跟 RocksDB 拿資料
                    Rockdis rockdisInstance = request.getServerContext().getSecondRockdisInstance();
                    try {
                        result = rockdisInstance.hget(key.toString(), field.toString());
                    } catch (RocksDBException e) {
                        e.printStackTrace();
                    }
                }

                logger.debug("Data info -> key : {}, field : {}, value : {}", key, field, result);
                if (result == null) {
                    return string("null");
                } else {
                    return string(result);
                }

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
