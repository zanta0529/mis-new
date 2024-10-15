package redisapi;

import com.github.tonivade.resp.command.CommandSuite;

public class CustomCommand extends CommandSuite {

    public CustomCommand() {
        super();
    }

    @Override
    protected void addCommand(Class<?> clazz) {
        super.addCommand(clazz);
    }
}
