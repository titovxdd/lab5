package Commands;

import managers.Commands;
import sup.Console;
import sup.Status;

public class Help extends Command{
    private final Commands commands;

    public Help(Console console, Commands commands) {
        super("help", "вывести справку по доступным командам", console);
        this.commands = commands;
    }

    @Override
    public Status execute(String arg){
        console.println("Список доступных команд:");
        for (var command : commands.getCommandsMap().entrySet()) {
            console.println(command.getValue().getName() + " - " + command.getValue().getDescription());
        }
        return new Status(true, "Справка по командам успешно выведена");
    }

    @Override
    public Status parse(String arg){
        return new Status(true, " ");
    }
}
