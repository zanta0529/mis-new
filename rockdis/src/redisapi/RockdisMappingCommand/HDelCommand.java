package redisapi.RockdisMappingCommand;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redisapi.Util.ChannelStatusUtil;
import rocksdbapi.Rockdis.RockdisProtocol;

import java.util.LinkedList;
import java.util.List;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.responseOk;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * <p> Since: 2023/12/18 </p>
 *
 * @author kkw
 * @version 1.0
 */
@Command("hdel")
public class HDelCommand implements RespCommand {

    private static final Logger logger = LoggerFactory.getLogger(HMGetCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {

            if (!isParameterValid(request.getLength())) {
                return error("ERR wrong number of arguments for 'hdel' command");
            }

            if (ChannelStatusUtil.isAuthorized(request)) {

                logger.debug(" [HDEL Command triggered] Cache System Status : " + request.getServerContext().getCacheSystemStatus());

                // 遇到 flushall 正在移轉 Cache
                while (request.getServerContext().getLargeCachePoolSystem().transitCache.get()) {
//                System.out.println("遇到 flushall 正在移轉 Cache");
                    continue;
                }

                List<String> hdelReturnList = new LinkedList<>();
                String[] hdelFieldArray = new String[request.getLength() - 2];
                for (int i = 0; i < request.getLength() - 2; i++) {
                    hdelFieldArray[i] = request.getParam(i + 2).toString();
                }

                if (request.getServerContext().getCacheSystemStatus()) {
                    // 有啟動 Cache System 則進行寫入
                    logger.debug("Enable Cache System.");

                    if (!request.getServerContext().getSafemode()) {
                        //刪除 LCPS內 Key的資料
                        request.getServerContext().getLargeCachePoolSystem().delFromLCPS(
                                request.getParam(1).toString(),
                                hdelFieldArray,
                                RockdisProtocol.Command.HDEL
                        );

                    } else {
                        // 已進入Safe Mode，僅能操作 Waiting Cache
                        request.getServerContext().getLargeCachePoolSystem().delFromWaitingCache(
                                request.getParam(1).toString(),
                                hdelFieldArray,
                                RockdisProtocol.Command.HDEL
                        );
                    }
                } else {
                    if (!request.getServerContext().getSafemode()) {
                        // 沒有啟動 Cache System 則直接寫入 RocksDB
                        try {
                            request.getServerContext().getRockdisInstance().hdel(
                                    request.getParam(1).toString(),
                                    hdelFieldArray
                            );
                        } catch (RocksDBException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // 已進入Safe Mode，僅能操作 Waiting Cache
                        request.getServerContext().getLargeCachePoolSystem().delFromWaitingCache(
                                request.getParam(1).toString(),
                                hdelFieldArray,
                                RockdisProtocol.Command.HDEL
                        );
                    }
                }

            } else {
                return error("Auth Failed");
            }

            return RedisToken.integer(0);
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
