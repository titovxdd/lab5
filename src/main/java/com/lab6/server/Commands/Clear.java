package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import sup.Console;
import sup.ExecutionStatus;

public class Clear extends Command {
    private final CollectionManager collectionManager;

    public Clear(Console console, CollectionManager collectionManager){
        super("clear", "очистить коллекцию", console);
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionStatus execute(String arg){
        collectionManager.clear();
        return new ExecutionStatus(true, "Коллекция успешно очищена");
    }

}
