package com.lab6.server.Commands;

import com.lab6.server.managers.Commands;
import sup.Console;
import sup.ExecutionStatus;

public class Help extends Command{
    private final Commands commands;

    public Help(Console console, Commands commands) {
        super("help", "вывести справку по доступным командам", console);
        this.commands = commands;
    }

    @Override
    public ExecutionStatus execute(String arg){
        console.println("Список доступных команд:");
        for (var command : commands.getCommandsMap().entrySet()) {
            console.println(command.getValue().getName() + " - " + command.getValue().getDescription());
        }
        return new ExecutionStatus(true, "Справка по командам успешно выведена");
    }

    @Override
    public ExecutionStatus parse(String arg){
        return new ExecutionStatus(true, " ");
    }
}
