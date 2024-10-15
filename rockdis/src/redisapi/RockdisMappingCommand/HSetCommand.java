package redisapi.RockdisMappingCommand;

import rocksdbapi.Rockdis.Rockdis;
import static rocksdbapi.Rockdis.RockdisProtocol.combineDBKeyWithKeyField;

import redisapi.Util.ChannelStatusUtil;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import org.rocksdb.RocksDBException;

import static com.github.tonivade.resp.protocol.RedisToken.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/9/24
 */

@Command("hset")
@ParamLength(3)
public class HSetCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(HSetCommand.class);
    @Override
    public RedisToken execute(Request request) {

        try {

            // 用來確保 HMSet 輸入的參數數量是正確的
            int requestLength = request.getLength();
            if (!isParameterValid(requestLength)) {
                return error("ERR wrong number of arguments for 'hset' command");
            }

            if (ChannelStatusUtil.isAuthorized(request)) {
                SafeString timestamp = request.getParam(0);
                SafeString key = request.getParam(1);
                SafeString field = request.getParam(2);
                SafeString value = request.getParam(3);


                // 遇到 flushall 正在移轉 Cache
                while (request.getServerContext().getLargeCachePoolSystem().transitCache.get()) {
//                System.out.println("遇到 flushall 正在移轉 Cache");
                    continue;
                }

                logger.debug(" [HSET Command triggered] Cache System Status : " + request.getServerContext().getCacheSystemStatus());

                // 實際開始進行寫入
                // Cache Mechanism
                if (request.getServerContext().getCacheSystemStatus()) {
                    // todo 這裡用來debug用
                    if (key.toString().equals("stockquote_20230810_otc_5291.tw_20230810")) {
                        System.out.println("HSET Key : " + key.toString() + " , Field : " + field.toString() + " , Value : " + value.toString());
                    }

//                logger.debug("Cache System : Enabled.");

                    if (!request.getServerContext().getSafemode()) {
                        // 一般情況下有啟動 Cache
                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        fieldValueMap.put(field.toString(), value.toString());
                        // 這裡設定 hmset是避免 同一個 key 在還沒有flush RocksDB前，
                        // 先寫入 hmset再寫入hset 這樣會 command會變成 hset
                        fieldValueMap.put("rockscommand", "hmset");
                        request.getServerContext().getLargeCachePoolSystem().putToLCPS(key.toString(), fieldValueMap);

                    } else {
                        // 啟動 flushall 切換 DB後，要將 request 先把留在 Cache中，等待轉換完畢寫入DB
                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        fieldValueMap.put(field.toString(), value.toString());
                        // 這裡設定 hmset是避免 同一個 key 在還沒有flush RocksDB前，
                        // 先寫入 hmset再寫入hset 這樣會 command會變成 hset
                        fieldValueMap.put("rockscommand", "hmset");
                        request.getServerContext().getLargeCachePoolSystem().putToWaitingCache(key.toString(), fieldValueMap);
                    }

                } else {

                    if (!request.getServerContext().getSafemode()) {
                        // 一般情況下，沒有啟動 Cache
                        Rockdis rockdisInstance = request.getServerContext().getRockdisInstance();
                        try {
                            rockdisInstance.hset(key.toString(), field.toString(), value.toString());
                        } catch (RocksDBException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 雖然是直接寫入到RocksDB，但是因為有 flushall 轉換DB行為，
                        // 因此要先把寫入的狀態保留在 Cache中，等待轉換完畢後寫入DB
                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        fieldValueMap.put(field.toString(), value.toString());
                        // 這裡設定 hmset是避免 同一個 key 在還沒有flush RocksDB前，
                        // 先寫入 hmset再寫入hset 這樣會 command會變成 hset
                        fieldValueMap.put("rockscommand", "hmset");
                        request.getServerContext().getLargeCachePoolSystem().putToWaitingCache(key.toString(), fieldValueMap);
                    }
                }

                return integer(1);
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
        return requestLength == 4;
    }
}
