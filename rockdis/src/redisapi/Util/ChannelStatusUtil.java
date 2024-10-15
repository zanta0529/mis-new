package redisapi.Util;

import com.github.tonivade.resp.Model.ChannelStatus;
import com.github.tonivade.resp.command.Request;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/25
 */

public class ChannelStatusUtil {

    public static boolean getAuthorizedStatus(Request request) {
    	if(request==null) return false;
        String channelID = request.getSession().getChannelId();
        ChannelStatus channelStatus = request.getServerContext().getChannelInfo(channelID);
        if(channelStatus==null) return false;
        return channelStatus.isAuthorized();
    }

    public static boolean setAuthorizedStatus(Request request, boolean status) {
        String channelID = request.getSession().getChannelId();
        ChannelStatus channelStatus = request.getServerContext().getChannelInfo(channelID);
        if(channelStatus==null) return false;
        channelStatus.setAuthorized(status);
        request.getServerContext().setChannelInfo(channelID, channelStatus);
        return true;
    }

    public static boolean isAuthorized(Request request) {
        return getAuthorizedStatus(request);
    }
}
