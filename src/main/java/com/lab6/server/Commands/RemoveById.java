package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import com.lab6.client.sup.Console;
import com.lab6.common.validators.IdValidator;
import com.lab6.common.Sup.ExecutionStatus;

public class RemoveById extends Command{

    public RemoveById(){
        super("remove_by_id", "удалить элемент из коллекции по его id", new IdValidator());
    }

    @Override
    public ExecutionStatus execute(String argument) {
        Long id = Long.parseLong(argument);
        if (collectionManager.getById(id) == null) {
            return new ExecutionStatus(false, "Элемент с указанным id не найден!");
        }
        collectionManager.removeById(id);
        return new ExecutionStatus(true, "Элемент успешно удален!");
    }
}
