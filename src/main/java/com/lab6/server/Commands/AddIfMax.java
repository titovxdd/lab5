package com.lab6.server.Commands;

import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.common.models.MusicBand;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class AddIfMax extends AskingCommand{

    public AddIfMax(){
        super("add_if_max", "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции", new EmptyValidator());
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
