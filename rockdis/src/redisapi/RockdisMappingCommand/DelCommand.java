package redisapi.RockdisMappingCommand;

import rocksdbapi.Rockdis.Rockdis;
import static rocksdbapi.Rockdis.RockdisProtocol.combineDBKeyWithKeyField;
import redisapi.Util.ChannelStatusUtil;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocksdbapi.Rockdis.RockdisProtocol;

import java.util.ArrayList;

import static com.github.tonivade.resp.protocol.RedisToken.error;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/13
 */

@Command("del")
public class DelCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(DelCommand.class);
    @Override
    public RedisToken execute(Request request) {

        try {

            int requestLength = request.getLength();

            ArrayList<String> keyArray = new ArrayList<>();
            for (int i = 1; i < requestLength; i++) {
                logger.debug("Would be deleted key : {}", request.getParam(i).toString());
                keyArray.add(request.getParam(i).toString());
            }
            logger.debug("Invoke DEL method. The count of key need to be deleted is : {}", keyArray.size());
//        SafeString key = request.getParam(1);
//        boolean status = false;
            int deleteCount = 0;

            if (ChannelStatusUtil.isAuthorized(request)) {

                // 遇到 flushall 正在移轉 Cache
                while (request.getServerContext().getLargeCachePoolSystem().transitCache.get()) {
                    System.out.println("遇到 flushall 正在移轉 Cache");
                    continue;
                }

                // 確認是否有啟動 Cache System
                if (request.getServerContext().getCacheSystemStatus()) {

                    if (!request.getServerContext().getSafemode()) {
                        //刪除 LCPS內 Key的資料
                        for (String key : keyArray) {
                            int count = request.getServerContext().getLargeCachePoolSystem().delFromLCPS(key, null, RockdisProtocol.Command.DELETE);
                            deleteCount += count;
                        }
                    } else {

                        for (String key : keyArray) {
                            int count = request.getServerContext().getLargeCachePoolSystem().delFromWaitingCache(key, null, RockdisProtocol.Command.DELETE);
                            deleteCount += count;
                        }
                    }

                } else {

                    if (!request.getServerContext().getSafemode()) {
                        // 一般情況下，沒有啟動 Cache
                        //沒有啟動Cache System 直接往 RocksDB送 DEL method
                        //todo 這裡要調整兩個地方
                        // 1.return 要有count
                        // 2.要確認rocksdb delete 有落實全部 key都有砍而不是只砍一個
                        for (String key : keyArray) {
                            Rockdis rockdisInstance = request.getServerContext().getRockdisInstance();
                            try {
                                int count = rockdisInstance.delete(key.toString());
                                deleteCount += count;
                            } catch (RocksDBException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        for (String key : keyArray) {
                            int count = request.getServerContext().getLargeCachePoolSystem().delFromWaitingCache(key, null, RockdisProtocol.Command.DELETE);
                            deleteCount += count;
                        }
                    }
                }

                logger.debug("Total deleted {} keys.", deleteCount);
                return RedisToken.integer(deleteCount);
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
}
