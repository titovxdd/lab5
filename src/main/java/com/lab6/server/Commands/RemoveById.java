package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import sup.Console;
import com.lab6.common.validators.IdValidator;
import sup.ExecutionStatus;

public class RemoveById extends Command{
    private final CollectionManager collectionManager;

    public RemoveById(Console console, CollectionManager collectionManager){
        super("remove_by_id", "удалить элемент из коллекции по его id", console);
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionStatus execute(String arg) {
        Long id = Long.parseLong(arg);
        collectionManager.removeById(id);
        return new ExecutionStatus(true, "Элемент успешно удален");
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
