package com.lab6.server.Commands;

import com.lab6.common.Sup.Pair;
import com.lab6.server.managers.CollectionManager;
import com.lab6.common.models.MusicBand;
import com.lab6.client.sup.Console;
import com.lab6.common.validators.IdValidator;
import com.lab6.common.Sup.ExecutionStatus;

public class Update extends AskingCommand{

    public Update(){
        super("update", "обновить значение элемента коллекции, id которого равен заданному", new IdValidator());
    }

    @Override
    public ExecutionStatus execute(MusicBand band, Pair<String, String> user){
        return collectionManager.update(band, user);
    }
}
