package redisapi.RockdisMappingCommand;

import rocksdbapi.Rockdis.Rockdis;
import redisapi.Util.ChannelStatusUtil;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static redisapi.Util.ArithmeticUtil.*;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.integer;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/5
 */

@Command("zremrangebyscore")
public class ZRemrangeByScoreCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(ZRemrangeByScoreCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {

            Rockdis rockdisInstance = request.getServerContext().getRockdisInstance();
            SafeString minScore = request.getParam(2);
            SafeString maxScore = request.getParam(3);

            Double minDoubleScore = 0d;
            Double maxDoubleScore = 0d;

            try {
                maxDoubleScore = SafeStringToDouble(maxScore);
                minDoubleScore = SafeStringToDouble(minScore);
            } catch (NumberFormatException exception) {
                return error("ERR value is not an integer or out of range");
            }

            if (!isParameterPositiveNumber(minDoubleScore) || !isParameterPositiveNumber(maxDoubleScore)) {
                return error("ERR MAX MIN value currently only can support positive integer");
            }

            if (minDoubleScore >= maxDoubleScore) {
                return error("Input variable is wrong, min score can not grater than max score.");
            }


            if (request.getLength() != 4) {
                return error("ZREMRANGEBYSCORE 輸入參數錯誤");
            }


            if (ChannelStatusUtil.isAuthorized(request)) {

                SafeString key = request.getParam(1);
                Integer returnCount = 0;

                try {

                    returnCount = rockdisInstance.zremrangebyscore(
                            key.toString().getBytes(),
                            roundDoubleNumberToInt(minDoubleScore),
                            roundDoubleNumberToInt(maxDoubleScore)
                    );
                } catch (NumberFormatException exception) {
                    return RedisToken.error(exception.toString());
                } catch (RocksDBException e) {
                    e.printStackTrace();
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
}
