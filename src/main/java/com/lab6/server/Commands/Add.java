package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import models.MusicBand;
import sup.Console;
import sup.ExecutionStatus;

public class Add extends AskingCommand{

    public Add(Console console, CollectionManager collectionManager){
        super("add", "добавить новый элемент в коллекцию", console, collectionManager);
    }

    @Override
    public ExecutionStatus execute(MusicBand band) {
        collectionManager.add(band);
        return new ExecutionStatus(true, "Элемент успешно добавлен в коллекцию");
    }
}
