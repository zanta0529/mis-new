package redisapi.RockdisMappingCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocksdbapi.Rockdis.Rockdis;
import redisapi.Util.ChannelStatusUtil;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import org.rocksdb.RocksDBException;

import java.util.ArrayList;
import java.util.List;

import static com.github.tonivade.resp.protocol.RedisToken.*;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/12/8
 */

@Command("keys")
@ParamLength(1)
public class KeyCommand implements RespCommand {

    private static final Logger logger = LoggerFactory.getLogger(KeyCommand.class);
    @Override
    public RedisToken execute(Request request) {

        try {

            if (ChannelStatusUtil.isAuthorized(request)) {

                SafeString pattern = request.getParam(1);
                Rockdis rockdisInstance = request.getServerContext().getSecondRockdisInstance();
                List<String> result = new ArrayList<>();
                try {
                    result = rockdisInstance.getKeys(pattern.toString());
                } catch (RocksDBException e) {
                    e.printStackTrace();
                }

                List<RedisToken> returnRedisToken = new ArrayList<>();
                for (String element : result) {
                    returnRedisToken.add(string(element));
                }
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
}
