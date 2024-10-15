package redisapi.E2E;

import com.github.tonivade.resp.command.CommandSuite;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/12/10
 */

public class E2ECustomCommand extends CommandSuite {
    public E2ECustomCommand() {
        super();
    }

    @Override
    protected void addCommand(Class<?> clazz) {
        super.addCommand(clazz);
    }
}
