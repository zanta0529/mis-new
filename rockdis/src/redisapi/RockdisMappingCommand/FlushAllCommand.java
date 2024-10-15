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

@Command("flushall")
@ParamLength(1)
public class FlushAllCommand implements RespCommand {
    private static final Logger logger = LoggerFactory.getLogger(FlushAllCommand.class);
    @Override
    public RedisToken execute(Request request) {

        try {

        	if (ChannelStatusUtil.isAuthorized(request)) {
                SafeString FlushAllParameter = request.getParam(1);
                logger.debug("Input string => {}", FlushAllParameter);

                // 啟動 safe mode
                System.out.println(getFullyDateTimeStr()+" 設定 Safe Mode 為 true");
                request.getServerContext().setSafemode(true);
                // 進行 flush
                request.getServerContext().setFlushAll();
                // 關閉 safe mode，準備上線

                System.out.println(getFullyDateTimeStr()+" 完成 Flush 設定 Safe Mode 為 false");
                request.getServerContext().setSafemode(false);
                // 解除 Set類別操作的阻擋，完成上線

                System.out.println(getFullyDateTimeStr()+" 將 Transit Cache 設為 false，準備上線");
                request.getServerContext().getLargeCachePoolSystem().transitCache.set(false);
                System.out.println(getFullyDateTimeStr()+" 上線完成");

                if (FlushAllParameter != null) {
                    return status(FlushAllParameter.toString());
                } else {
                    return status("FLUSHALL");
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
    }
    
    public static String getFullyDateTimeStr() {
        java.sql.Timestamp now = new java.sql.Timestamp(System.
                currentTimeMillis());
        String nowStr = now.toString();
        while (nowStr.length() < 23)
            nowStr = nowStr + "0";
        //System.out.println(nowStr);

        String timeStr = nowStr.substring(0, 4);
        timeStr += nowStr.substring(5, 7);
        timeStr += nowStr.substring(8, 10);
        timeStr += nowStr.substring(11, 13);
        timeStr += nowStr.substring(14, 16);
        timeStr += nowStr.substring(17, 19);
        timeStr += nowStr.substring(20, 23);
        return timeStr;
    }    
}
