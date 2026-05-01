package com.lab6.server.Commands;

import com.lab6.common.Sup.Pair;
import com.lab6.common.validators.ArgumentValidator;
import com.lab6.common.models.MusicBand;
import com.lab6.common.validators.IdValidator;
import com.lab6.common.Sup.ExecutionStatus;

public abstract class AskingCommand extends Command{

    public AskingCommand(String name, String description, ArgumentValidator argumentValidator) {
        super(name, description, argumentValidator);
    }

    public ExecutionStatus run(String arg, MusicBand band, Pair<String, String> user) {
        ExecutionStatus argumentStatus = getArgumentValidator().validate(arg, getName());
        if (argumentStatus.isSuccess()) {
            ExecutionStatus permissionStatus = checkPermission(user);
            if (!permissionStatus.isSuccess()) {
                return permissionStatus;
            }
            if (getArgumentValidator() instanceof IdValidator) {
                Long id = Long.parseLong(arg);
                if (collectionManager.getById(id) == null) {
                    return new ExecutionStatus(false, "Элемент с указанным id не найден");
                }
                band.updateId(id);
            }
            return execute(band, user);
        } else {
            return argumentStatus;
        }
    }
    @Override
    public ExecutionStatus execute(String arg, Pair<String, String> user) {
        return null;
    }

    public abstract ExecutionStatus execute(MusicBand band, Pair<String, String> user);

    @Override
    public ExecutionStatus run(String arg, Pair<String, String> user) {
        return new ExecutionStatus(false, "Метод должен вызываться с аргументом MusicBand");
    }
}
