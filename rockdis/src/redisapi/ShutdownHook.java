package redisapi;

import com.github.tonivade.resp.RespServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kkw
 * @project redis-practise
 * @date 2021/4/7
 */

public class ShutdownHook extends Thread{
    private static Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private RespServer respServer;
    public ShutdownHook(RespServer respServer){
        this.respServer = respServer;
    }
    @Override
    public void run() {
        logger.debug("Encounter abnormal interrupt. Start to shutdown process.");
        respServer.stop();
        logger.debug("Shutdown completely.");
    }
}
