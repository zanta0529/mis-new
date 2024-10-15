package redisapi.RockdisMappingCommand;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import java.util.ArrayList;
import java.util.List;

import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

import redisapi.Util.ChannelStatusUtil;

@Command("config")
public class ConfigCommand implements RespCommand {

	@Override
	public RedisToken execute(Request request) {
		
        if (!isParameterValid(request.getLength())) {
            return error("ERR wrong number of arguments for 'config' command");
        }
		
        if (ChannelStatusUtil.isAuthorized(request)) {
        	String subCommand = request.getParam(1).toString().toLowerCase();
        	String subKey = request.getParam(2).toString().toLowerCase();
        	
        	//System.out.println("----->"+subCommand.toString()+" "+subKey.toString());
        	
            List<RedisToken> returnRedisToken = new ArrayList<>();  
        	if(subCommand.equals("get")) {   
        		if(subKey.equals("timeout")) {
            		List<String> hmgetReturnList = new ArrayList<String>();
            		hmgetReturnList.add(subKey);
            		hmgetReturnList.add("0");
                    // TODO 由於一開始並沒有串接好 redis server與redis client。所以必須從redis server 取得的資訊在進行 re-process
                    for (int i = 0; i < hmgetReturnList.size(); i++) {
                        returnRedisToken.add(RedisToken.string(hmgetReturnList.get(i)));
                    }
   			
        		} else if(subKey.equals("maxclients")) {
            		List<String> hmgetReturnList = new ArrayList<String>();
            		hmgetReturnList.add(subKey);
            		hmgetReturnList.add("10000");
                    // TODO 由於一開始並沒有串接好 redis server與redis client。所以必須從redis server 取得的資訊在進行 re-process

                    for (int i = 0; i < hmgetReturnList.size(); i++) {
                        returnRedisToken.add(RedisToken.string(hmgetReturnList.get(i)));
                    }
        		}
        	}
            return array(returnRedisToken); 
        } else {
        	return error("Auth Failed");
        }
        
		// TODO Auto-generated method stub
		//return null;
	}

	
    private boolean isParameterValid(int requestLength) {
        // config 的參數數量
        return requestLength == 3;
    }
}
