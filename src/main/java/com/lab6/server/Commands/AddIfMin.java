package com.lab6.server.Commands;

import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.common.models.MusicBand;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class AddIfMin extends AskingCommand{
    public AddIfMin(){
        super("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции", new EmptyValidator());
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
