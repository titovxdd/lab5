package com.lab6.server.Commands;

import com.lab6.common.Sup.Pair;
import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class Clear extends Command {

    public Clear(){
        super("clear", "очистить коллекцию", new EmptyValidator());
    }

    @Override
    public ExecutionStatus execute(String arg, Pair<String, String> user){
        collectionManager.clear(user);
        return new ExecutionStatus(true, "Коллекция успешно очищена");
    }

}
