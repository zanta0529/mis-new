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

@Command("zremrangebyrank")
public class ZRemrangeByRankCommand implements RespCommand {

    private static Logger logger = LoggerFactory.getLogger(ZRemrangeByRankCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {

            Rockdis rockdisInstance = request.getServerContext().getRockdisInstance();
            SafeString startRank = request.getParam(2);
            SafeString stopRank = request.getParam(3);

            Integer startIntegerRank = 0;
            Integer stopIntegerRank = 0;


            // 參數處理一定要寫在最前面
            if (request.getLength() != 4) {
                return error("ZREMRANGEBYRANK 輸入參數錯誤");
            }
            try {
                stopIntegerRank = SafeStringToInt(stopRank);
                startIntegerRank = SafeStringToInt(startRank);
            } catch (NumberFormatException exception) {
                return error("ERR value is not an integer or out of range");
            }


            // start 大於 stop 就 return 0
            if (startIntegerRank > stopIntegerRank) {
                return integer(0);
//            return error("Input variable is wrong, min score can not grater than max score.");
            }


            if (ChannelStatusUtil.isAuthorized(request)) {

                SafeString key = request.getParam(1);
                Integer returnCount = 0;

                try {

                    returnCount = rockdisInstance.zremrangebyrank(
                            key.toString().getBytes(),
                            startIntegerRank,
                            stopIntegerRank
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
