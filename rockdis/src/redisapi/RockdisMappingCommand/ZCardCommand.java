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

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/6
 */

@Command("zcard")
@ParamLength(1)
public class ZCardCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(ZCardCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {

            if (ChannelStatusUtil.isAuthorized(request)) {
                SafeString key = request.getParam(1);
                Rockdis rockdisInstance = request.getServerContext().getSecondRockdisInstance();
                Integer resultCount = 0;
                try {
                    resultCount = rockdisInstance.zcard(key.toString().getBytes());
                } catch (RocksDBException e) {
                    e.printStackTrace();
                }

                return integer(resultCount);

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
}
