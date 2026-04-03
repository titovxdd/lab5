package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import models.MusicBand;
import sup.Console;
import sup.ExecutionStatus;

public class AddIfMin extends AskingCommand{
    public AddIfMin(Console console, CollectionManager collectionManager){
        super("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции", console, collectionManager);
    }

    @Override
    public ExecutionStatus execute(MusicBand band){
        if (collectionManager.getCollection().isEmpty()) {
            collectionManager.add(band);
            return new ExecutionStatus(true, "Коллекция пуста. Элемент добавлен как наименьший");
        }
        if (band.compareTo(collectionManager.head())< 0){
            collectionManager.add(band);
            return new ExecutionStatus(true, "Элемент успешно добавлен в коллекцию");
        } else {
            return new ExecutionStatus(true, "Элемент не является наименьшим в коллекции");
        }
    }
}
