package redisapi.RockdisMappingCommand;

import redisapi.Util.AuthUtil;
import redisapi.Util.ChannelStatusUtil;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.status;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/9/11
 */

@Command("auth")
@ParamLength(1)
public class AuthCommand implements RespCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthCommand.class);
    @Override
    public RedisToken execute(Request request) {
        SafeString password = request.getParam(1);
        LOGGER.debug("Auth string => {}", password);

        if (AuthUtil.hasAuthorized(request, password.toString())) {
            boolean auth = ChannelStatusUtil.setAuthorizedStatus(request, true);
            if(auth) return status("Auth OK");
            else return error("Auth Failed.");
        } else {
            ChannelStatusUtil.setAuthorizedStatus(request, false);
            return error("Auth Failed.");
        }
    }
}
