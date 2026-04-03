package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import sup.Console;
import sup.ExecutionStatus;

public class Info extends Command{
    private final CollectionManager collectionManager;

    public Info(Console console, CollectionManager collectionManager){
        super("info", "вывести в стандартный поток вывода информацию о коллекции", console);
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionStatus parse(String arg){
        return new ExecutionStatus(true, " ");
    }

    @Override
    public ExecutionStatus execute(String arg){
        console.println("Тип коллекции: " + collectionManager.getCollection().getClass().getName());
        console.println("Дата инициализации: " + collectionManager.getInitializationDate());
        console.println("Дата последнего сохранения: " + collectionManager.getLastSaveDate());
        console.println("Количество элементов: " + collectionManager.getCollection().size());
        return new ExecutionStatus(true, "Информация о коллекции успешно выведена");
    }
}
