package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import sup.Console;
import sup.ExecutionStatus;

public class Save extends Command{
    private final CollectionManager collectionManager;

    public Save(Console console, CollectionManager collectionManager){
        super("save", "сохранить коллекцию в файл", console);
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionStatus execute(String arg) {
        collectionManager.saveCollection();
        return new ExecutionStatus(true, "Коллекция успешно сохранена");
    }
}
