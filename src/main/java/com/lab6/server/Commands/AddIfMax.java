package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import models.MusicBand;
import sup.Console;
import sup.ExecutionStatus;

public class AddIfMax extends AskingCommand{

    public AddIfMax(Console console, CollectionManager collectionManager){
        super("add_if_max", "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции", console, collectionManager);
    }

    public ExecutionStatus execute(MusicBand band){
        if (collectionManager.getCollection().isEmpty()) {
            collectionManager.add(band);
            return new ExecutionStatus(true, "Коллекция пуста. Элемент добавлен как наибольший");
        }
        MusicBand maxBand = null;
        for (MusicBand b : collectionManager.getCollection()) {
            if (maxBand == null || b.getId() > maxBand.getId()) {
                maxBand = b;
            }
        }
        if (band.compareTo(maxBand) > 0){
            collectionManager.add(band);
            return new ExecutionStatus(true, "Элемент успешно добавлен в коллекцию");
        } else {
            return new ExecutionStatus(true, "Элемент не является наибольшим в коллекции");
        }
    }
}
