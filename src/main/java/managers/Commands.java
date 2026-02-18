package managers;

import java.util.HashMap;
import java.util.Map;
import Commands.Command;

public class Commands {
    private static Map<String, Command> commands = new HashMap<>();

    public static void register(String commandName, Command command) {
        commands.put(commandName, command);
    }

    public Map<String, Command> getCommandsMap() {
        return commands;
    }

    public static Command getCommand(String commandName) {
        return commands.get(commandName);
    }
}
