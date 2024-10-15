package redisapi.E2E;


import com.github.tonivade.resp.RespServer;
import redisapi.RockdisMappingCommand.*;


/**
 * @author kkw
 * @project redis-practise
 * @date 2020/12/10
 */

public class StartRockdisServer {

    public E2ECustomCommand setCommand(){
        E2ECustomCommand customCommand = new E2ECustomCommand();
        customCommand.addCommand(AuthCommand.class);
        customCommand.addCommand(SetCommand.class);
        customCommand.addCommand(GETCommand.class);
        customCommand.addCommand(HSetCommand.class);
        customCommand.addCommand(HGetCommand.class);
        customCommand.addCommand(HMSetCommand.class);
        customCommand.addCommand(HMGetCommand.class);
        customCommand.addCommand(HGetAllCommand.class);
        customCommand.addCommand(hincrByCommand.class);
        customCommand.addCommand(ZAddCommand.class);
        customCommand.addCommand(ZCardCommand.class);
        customCommand.addCommand(ZRangeCommand.class);
        customCommand.addCommand(ZRangeByScoresCommand.class);
        customCommand.addCommand(ZRevrangeCommand.class);
        customCommand.addCommand(ZRevrangeByScoreCommand.class);
        customCommand.addCommand(KeyCommand.class);
        customCommand.addCommand(DelCommand.class);

        return customCommand;
    }

    public void invokeRespServer(String ip, int port, String primaryDir, String secondDir) {
        RespServer server = RespServer.builder()
                .host(ip).port(port)
                .persistenceDir(primaryDir, secondDir)
                .isAuthEnabled(true).getAuthString("admin")
                .commands(setCommand()).build();
        server.start();
    }
}
