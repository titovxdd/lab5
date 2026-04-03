package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import sup.Console;
import sup.ExecutionStatus;

public class Head extends Command{
    private final CollectionManager collectionManager;

    public Head(Console console, CollectionManager collectionManager){
        super("head", "вывести первый элемент коллекции", console);
        this.collectionManager = collectionManager;
    }

    public ExecutionStatus execute(String arg){
        if (collectionManager.getCollection().isEmpty()){
            return new ExecutionStatus(false, "Коллекция пуста");
        }
        console.println(collectionManager.head().toString());
        return new ExecutionStatus(true, "Выведен первый элемент коллекции");
    }
}
