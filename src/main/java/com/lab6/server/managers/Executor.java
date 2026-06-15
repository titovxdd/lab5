package com.lab6.server.managers;

import com.lab6.common.Sup.Pair;
import com.lab6.common.validators.ArgumentValidator;
import com.lab6.server.Commands.AskingCommand;
import com.lab6.server.Commands.Command;
import com.lab6.server.Server;
import com.lab6.common.models.MusicBand;
import com.lab6.common.Sup.ExecutionStatus;

public class Executor implements ExecutorInterface{
    private final CommandManager commandManager;

    public Executor(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    private ExecutionStatus validateCommand(String[] userCommand) {
        try {
            Command command = commandManager.getCommand(userCommand[0]);
            if (command == null) {
                return new ExecutionStatus(false, "Команда '" + userCommand[0] + "' не найдена. Для показа списка команд введите 'help'.");
            } else {
                ArgumentValidator argumentValidator = command.getArgumentValidator();
                return argumentValidator.validate(userCommand[1].trim(), command.getName());
            }
        } catch (NullPointerException e) {
            return new ExecutionStatus(false, "Введено недостаточно аргументов для выполнения последней команды.");
        }
    }

    @Override
    public ExecutionStatus runCommand(String[] userCommand, MusicBand musicBand, Pair<String, String> user) {
        ExecutionStatus validateStatus = validateCommand(userCommand);
        if (validateStatus.isSuccess()) {
            var command = commandManager.getCommand(userCommand[0]);
            Server.logger.info("Command '" + userCommand[0] + "' is running...");
            if (AskingCommand.class.isAssignableFrom(command.getClass())) {
                return ((AskingCommand) command).run(userCommand[1], musicBand, user);
            } else {
                return command.run(userCommand[1], user);
            }
        } else {
            return new ExecutionStatus(false, validateStatus.getMessage());
        }
    }
}
