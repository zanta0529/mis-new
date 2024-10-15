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

import java.util.ArrayList;
import java.util.List;

import static redisapi.Util.ArithmeticUtil.*;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/13
 */

@Command("zrevrangebyscore")
@ParamLength(3)
public class ZRevrangeByScoreCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(ZRevrangeByScoreCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {

            if (ChannelStatusUtil.isAuthorized(request)) {
                SafeString key = request.getParam(1);
                SafeString maxScore = request.getParam(2);
                SafeString minScore = request.getParam(3);

                Double maxDoubleScore = 0d;
                Double minDoubleScore = 0d;
                try {
                    maxDoubleScore = SafeStringToDouble(maxScore);
                    minDoubleScore = SafeStringToDouble(minScore);
                } catch (NumberFormatException exception) {
                    return error("ERR value is not an integer or out of range");
                }

                if (!isParameterPositiveNumber(maxDoubleScore) || !isParameterPositiveNumber(minDoubleScore)) {
                    return error("ERR MAX MIN value currently only can support positive integer");
                }

                List<byte[]> zrevrangeByScoreList = new ArrayList<>();
                Rockdis rockdisInstance = request.getServerContext().getSecondRockdisInstance();
                try {
                    zrevrangeByScoreList = rockdisInstance.zrevrangebyscore(
                            key.toString().getBytes(),
                            roundDoubleNumberToInt(maxDoubleScore),
                            roundDoubleNumberToInt(minDoubleScore)
                    );
                } catch (RocksDBException e) {
                    e.printStackTrace();
                }
                return array(ArithmeticUtil.byteArrayListToRedisTokenList(zrevrangeByScoreList));
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
