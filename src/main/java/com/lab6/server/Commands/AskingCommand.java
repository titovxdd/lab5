package com.lab6.server.Commands;

import com.lab6.common.validators.ArgumentValidator;
import com.lab6.common.models.MusicBand;
import com.lab6.common.validators.IdValidator;
import com.lab6.common.Sup.ExecutionStatus;

public abstract class AskingCommand extends Command{

    public AskingCommand(String name, String description, ArgumentValidator argumentValidator) {
        super(name, description, argumentValidator);
    }

    public ExecutionStatus run(String arg, MusicBand band) {
        ExecutionStatus argumentStatus = getArgumentValidator().validate(arg);
        if (argumentStatus.isSuccess()) {
            Long id;
            if (getArgumentValidator() instanceof IdValidator) {
                id = Long.parseLong(arg);
                if (collectionManager.getById(id) == null) {
                    return new ExecutionStatus(false, "Элемент с указанным id не найден");
                }
            } else {
                id = collectionManager.getFreeId();
            }
            band.updateId(id);
            return execute(band);
        } else {
            return argumentStatus;
        }
    }
    @Override
    public ExecutionStatus execute(String arg) {
        return null;
    }

    public abstract ExecutionStatus execute(MusicBand band);

    @Override
    public ExecutionStatus run(String arg) {
        return new ExecutionStatus(false, "Метод должен вызываться с аргументом MusicBand");
    }
}
