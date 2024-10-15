package redisapi.RockdisMappingCommand;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

import redisapi.Util.ChannelStatusUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.status;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/9/11
 */

@Command("ping")
@ParamLength(1)
public class PingCommand implements RespCommand {
    private static final Logger logger = LoggerFactory.getLogger(PingCommand.class);
    @Override
    public RedisToken execute(Request request) {

        try {
        	if (ChannelStatusUtil.isAuthorized(request)) {
                SafeString PingParameter = request.getParam(1);
//              System.out.println("Auth string => " + PingParameter);
                  logger.debug("Auth string => {}", PingParameter);

                  if (PingParameter != null) {
                      return status(PingParameter.toString());
                  } else {
                      return status("PONG");
                  }	
        	} else {
        		return error("Auth Failed");
        	}
        } catch (Exception e) {
            // 避免 process 因為 exception而被卡住，
            // 因為前端需要接收一個 resp return，所以遇到exception 就直接回傳 error
            logger.error(" 執行失敗，Request => {} \n 詳細 stack traced => ", request, e);
            return error("執行失敗，請查看 error log");
        }
//
//        if (AuthUtil.hasAuthorized(request, password.toString())) {
//            ChannelStatusUtil.setAuthorizedStatus(request, true);
//            return status("Auth OK");
//        } else {
//            ChannelStatusUtil.setAuthorizedStatus(request, false);
//            return error("Auth Failed.");
//        }
    }
}
