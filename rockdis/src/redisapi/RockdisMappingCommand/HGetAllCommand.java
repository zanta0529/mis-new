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

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

import static com.github.tonivade.resp.protocol.RedisToken.*;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/9/25
 */
@Command("hgetall")
@ParamLength(1)
public class HGetAllCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(HGetAllCommand.class);
    @Override
    public RedisToken execute(Request request) {

        try {

            // 用來確保 HMSet 輸入的參數數量是正確的
            int requestLength = request.getLength();
            if (!isParameterValid(requestLength)) {
                return error("ERR wrong number of arguments for 'hgetall' command");
            }

            if (ChannelStatusUtil.isAuthorized(request)) {
                SafeString key = request.getParam(1);

                logger.debug(" [HGETALL Command triggered] Cache System Status : " + request.getServerContext().getCacheSystemStatus());

                // 如果在切換DB，get相關的都回 null
                if (request.getServerContext().getSafemode() == true) {
                    return array(new ArrayList<>());
                }

                logger.debug("hgetall key is : {}", key.toString());
                Map<String, String> hgetallMap = new HashMap<>();

                // 確認是否有啟動 Cache System
                if (request.getServerContext().getCacheSystemStatus()) {
                    // 詢問LCPS是否有值
                    Object LCPSResult = request.getServerContext().getLargeCachePoolSystem().getAllFromLCPS(key.toString());
                    // 處理從 LCPS 取得的結果 (因為 LCPS 取得的結果可能是 null 或是 Map)
                    if (LCPSResult != null) {
                        //Cache 有值，所以直接return 該 Map
                        if (key.toString().equals("stockquote_20231123_tse_2330.tw_20231123")) {
                            System.out.println("[HGetAllCommand] LCPS has data, Start to retrieve from LCPS...");
                            System.out.println("===");
                            System.out.println();
                        }
                        hgetallMap = (Map<String, String>) LCPSResult;
                    } else {
                        // 因為 Cache沒有值，所以去 RocksDB後端拿看看有沒有值
                        logger.debug("LCPS has no data, Start to retrieve from rocksdb...");
                        if (key.toString().equals("stockquote_20231123_tse_2330.tw_20231123")) {
                            System.out.println("[HGetAllCommand] LCPS has no data, Start to retrieve from rocksdb...");
                            System.out.println("===");
                            System.out.println();
                        }

                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        hgetallMap = request.getServerContext().getLargeCachePoolSystem().putToLCPS(key.toString(), fieldValueMap);
                    }
                } else {
                    // 沒有啟動 Cache System，直接跟 RocksDB取值
                    // 先讓 取值 去 Primary RocksDB 取值, Secondary 感覺不穩定
                    Rockdis rockdisInstance = request.getServerContext().getSecondRockdisInstance();
                    logger.debug("Start to retrieve from rocksdb...");
                    try {
                        hgetallMap = rockdisInstance.hgetAll(key.toString());
                    } catch (RocksDBException e) {
                        e.printStackTrace();
                    }
                }

                if(hgetallMap==null) hgetallMap=new HashMap<>();
                logger.info("Finished retrieve from rocksdb...");
                logger.info("Result size : " + hgetallMap.size());

                // TODO 由於一開始並沒有串接好 redis server與redis client。所以必須從redis server 取得的資訊在進行 re-process
                logger.info("Re-Process Array");
                List<RedisToken> returnRedisToken = new ArrayList<>();
                for (Map.Entry entry : hgetallMap.entrySet()) {
                    // 用來避開輸出給使用者用來寫入RocksDB所埋入的 rockscommand key
                    if (!entry.getKey().toString().equals("rockscommand")) {
                        returnRedisToken.add(string(entry.getKey().toString()));
                        returnRedisToken.add(string(entry.getValue().toString()));
                    }
                }
                logger.info("Finished Re-process Array");
                logger.info("Start to return array to redis client.");
                
                //String aaa = key.toString();
                //if(aaa.indexOf("IX0189")!=-1) System.out.println(aaa+" __005 "+returnRedisToken);
                
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
        return requestLength == 2;
    }
}
