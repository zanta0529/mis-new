package redisapi.RockdisMappingCommand;

import rocksdbapi.Rockdis.Rockdis;
import redisapi.Util.ArithmeticUtil;
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

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/2
 */

@Command("hincrby")
@ParamLength(3)
public class hincrByCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(hincrByCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {

            if (ChannelStatusUtil.isAuthorized(request)) {
                logger.debug(" Auth Status is : {}", request.getServerContext().getAuthStatus());
                SafeString key = request.getParam(1);
                SafeString filed = request.getParam(2);
                SafeString incremental = request.getParam(3);

                Rockdis rockdisInstance = request.getServerContext().getRockdisInstance();
                Integer resultInt = null;
                try {
                    Integer incrementalInt = ArithmeticUtil.SafeStringToInt(incremental);
                    resultInt = rockdisInstance.hincrBy(key.toString(), filed.toString(), incrementalInt);


                } catch (NumberFormatException exception) {
                    return RedisToken.error(exception.toString());
                } catch (RocksDBException e) {
                    e.printStackTrace();
                }
                return RedisToken.integer(resultInt);
            } else {
                return RedisToken.error("Auth Failed");
            }
        } catch (Exception e) {
            // 避免 process 因為 exception而被卡住，
            // 因為前端需要接收一個 resp return，所以遇到exception 就直接回傳 error
            logger.error(" 執行失敗，Request => {} \n 詳細 stack traced => ", request, e);
            return error("執行失敗，請查看 error log");
        }
    }
}
