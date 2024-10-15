package redisapi.Util;

import com.github.tonivade.resp.command.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/9/11
 */

public class AuthUtil {

    private static final Logger logger = LoggerFactory.getLogger(AuthUtil.class);

    public static boolean hasAuthorized(Request request, String authString) {
        logger.debug("Auth String in Server Context is : {}", request.getServerContext().getAuthString());
        return authString.equals(request.getServerContext().getAuthString());
    }
}
