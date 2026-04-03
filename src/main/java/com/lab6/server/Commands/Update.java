package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import models.MusicBand;
import sup.Console;
import com.lab6.common.validators.IdValidator;
import sup.ExecutionStatus;

public class Update extends AskingCommand{

    public Update(Console console, CollectionManager collectionManager){
        super("update", "обновить значение элемента коллекции, id которого равен заданному", console, collectionManager);
    }

    @Override
    public ExecutionStatus execute(MusicBand band){
        collectionManager.removeById(band.getId());
        collectionManager.add(band);
        return new ExecutionStatus(true, "Элемент успешно обновлён!");
    }

    @Override
    public ExecutionStatus parse(String arg){
        IdValidator validator = new IdValidator(collectionManager);
        if (validator.validate(arg).isSuccess()){
            return new ExecutionStatus(true, "ID валидно");
        } else {
            return validator.validate(arg);
        }
    }
}
