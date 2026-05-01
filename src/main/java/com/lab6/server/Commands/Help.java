package com.lab6.server.Commands;

import com.lab6.common.Sup.Pair;
import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.CommandManager;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class Help extends Command{
    private final CommandManager commandManager;

    public Help(CommandManager commandManager) {
        super("help", "вывести справку по доступным командам", new EmptyValidator());
        this.commandManager = commandManager;
    }

    @Override
    public ExecutionStatus execute(String arg, Pair<String, String> user) {
        StringBuilder helpMessage = new StringBuilder("Список доступных команд:\n");
        for (var command : commandManager.getCommandsMap().entrySet()) {
            helpMessage.append(command.getValue().getName())
                    .append(" - ")
                    .append(command.getValue().getDescription())
                    .append("\n");
        }
        helpMessage.append("Справка по командам успешно выведена");
        return new ExecutionStatus(true, helpMessage.toString());
    }

}
