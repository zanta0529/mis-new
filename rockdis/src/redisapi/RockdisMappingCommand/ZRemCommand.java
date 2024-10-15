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
import rocksdbapi.Rockdis.RockdisProtocol;

import java.util.LinkedList;
import java.util.List;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/5
 */

@Command("zrem")
public class ZRemCommand implements RespCommand {

    private static final Logger logger = LoggerFactory.getLogger(ZRemCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {

            Rockdis rockdisInstance = request.getServerContext().getRockdisInstance();
            if (!isParameterValid(request.getLength())) {
                return error("ERR wrong number of arguments for 'zrem' command");
            }

            if (ChannelStatusUtil.isAuthorized(request)) {

                logger.debug(" [ZREM Command triggered] Cache System Status : " + request.getServerContext().getCacheSystemStatus());

                // 遇到 flushall 正在移轉 Cache
                while (request.getServerContext().getLargeCachePoolSystem().transitCache.get()) {
//                System.out.println("遇到 flushall 正在移轉 Cache");
                    continue;
                }

                SafeString key = request.getParam(1);
                SafeString member = request.getParam(2);
                Integer returnCount = 0;

                List<String> zremReturnList = new LinkedList<>();
                String[] zremMemberArray = new String[request.getLength() - 2];
                for (int i = 0; i < request.getLength() - 2; i++) {
                    zremMemberArray[i] = request.getParam(i + 2).toString();
                }

                if (request.getServerContext().getCacheSystemStatus()) {
                    logger.debug("Enable Cache System.");

                    if (!request.getServerContext().getSafemode()) {
                        //刪除 LCPS內 Key的資料
                        request.getServerContext().getLargeCachePoolSystem().delFromLCPS(
                                request.getParam(1).toString(),
                                zremMemberArray,
                                RockdisProtocol.Command.ZREM
                        );
                    } else {
                        // 已進入Safe Mode，僅能操作 Waiting Cache
                        request.getServerContext().getLargeCachePoolSystem().delFromWaitingCache(
                                request.getParam(1).toString(),
                                zremMemberArray,
                                RockdisProtocol.Command.ZREM
                        );
                    }
                } else {
                    if (!request.getServerContext().getSafemode()) {
                        // 沒有啟動 Cache System 則直接寫入 RocksDB
                        try {
                            request.getServerContext().getSecondRockdisInstance().zrem(
                                    request.getParam(1).toString(),
                                    zremMemberArray
                            );
                        } catch (RocksDBException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // 已進入Safe Mode，僅能操作 Waiting Cache
                        request.getServerContext().getLargeCachePoolSystem().delFromWaitingCache(
                                request.getParam(1).toString(),
                                zremMemberArray,
                                RockdisProtocol.Command.ZREM
                        );
                    }
                }
                return integer(returnCount);
            } else {
                return error("Auth Failed");
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
