package com.lab6.server.managers;

import com.lab6.common.validators.ArgumentValidator;
import com.lab6.server.Commands.AskingCommand;
import com.lab6.server.Commands.Command;
import com.lab6.server.Server;
import com.lab6.common.models.MusicBand;
import com.lab6.common.Sup.ExecutionStatus;

public class Executer {
    private final CommandManager commandManager;

    public Executer(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    private ExecutionStatus validateCommand(String[] userCommand) {
        try {
            Command command = commandManager.getCommand(userCommand[0]);
            if (command == null) {
                return new ExecutionStatus(false, "Команда '" + userCommand[0] + "' не найдена. Для показа списка команд введите 'help'.");
            } else {
                ArgumentValidator argumentValidator = command.getArgumentValidator();
                return argumentValidator.validate(userCommand[1].trim());
            }
        } catch (NullPointerException e) {
            return new ExecutionStatus(false, "Введено недостаточно аргументов для выполнения последней команды.");
        }
    }
    public ExecutionStatus runCommand(String[] userCommand, MusicBand musicBand) {
        ExecutionStatus validateStatus = validateCommand(userCommand);
        if (validateStatus.isSuccess()) {
            var command = commandManager.getCommand(userCommand[0]);
            Server.logger.info("Выполнение команды '" + userCommand[0] + "'");
            if (AskingCommand.class.isAssignableFrom(command.getClass())) {
                return ((AskingCommand) command).run(userCommand[1], musicBand);
            } else {
                return command.run(userCommand[1]);
            }
        } else {
            return new ExecutionStatus(false, validateStatus.getMessage());
        }
    }
}
