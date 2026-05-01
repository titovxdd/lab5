package com.lab6.server.Commands;

import com.lab6.common.Sup.Pair;
import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.common.models.MusicBand;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class Add extends AskingCommand{

    public Add(){
        super("add", "добавить новый элемент в коллекцию", new EmptyValidator());
    }

    @Override
    public ExecutionStatus execute(MusicBand band, Pair<String, String> user) {
        collectionManager.add(band, user);
        return new ExecutionStatus(true, "Элемент успешно добавлен в коллекцию");
    }
}
