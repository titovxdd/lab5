package com.lab6.server.managers;

import com.lab6.server.Commands.Command;
import sup.ExecutionStatus;

public class Executer {
    private final Commands commandManager;

    public Executer(Commands commandManager) {
        this.commandManager = commandManager;
    }

    private ExecutionStatus validateCommand(String[] userCommand) {
        try {
            Command command = commandManager.getCommand(userCommand[0]);
            if (command == null) {
                return new ExecutionStatus(false, "Команда '" + userCommand[0] + "' не найдена! Для показа списка команд введите 'help'.");
            } else {
                ArgumentValidator argumentValidator = command.getArgumentValidator();
                return argumentValidator.validate(userCommand[1].trim(), command.getName());
            }
        } catch (NullPointerException e) {
            return new ExecutionStatus(false, "Введено недостаточно аргументов для выполнения последней команды!");
        }
    }
    public ExecutionStatus runCommand(String[] args){
        try {
            var command = Commands.getCommand(args[0]);
            if (command == null){
                return new ExecutionStatus(false, "Команда '" + args[0] + "' не найдена");
            } else {
                if (command.parse(args[1]).isSuccess()) {
                    console.println("Выполнение команды '" + args[0] + "'");
                    return command.execute(args[1]);
                } else {
                    return command.parse(args[1]);
                }
            }
        }  catch (NullPointerException e) {
            return new ExecutionStatus(false, "Введено недостаточно аргументов для выполнения последней команды");
        } catch (Exception e) {
            return new ExecutionStatus(false, "Ошибка при выполнении команды");
        }
    }

    public void interactiveMode() {
        try {
            while (true){
                String[] args = (console.readln().trim() + " ").split(" ", 2);
                args[1] = args[1].trim();
                ExecutionStatus commandStatus = runCommand(args);
                if (commandStatus.isSuccess()) {
                    console.println(commandStatus.getMessage());
                } else {
                    console.printError(commandStatus.getMessage());
                }
            }
        } catch (Exception e) {
            console.printError("Произошла ошибка выполнения команды");
        }
    }
}
