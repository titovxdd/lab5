package com.lab6.server.Commands;

import com.lab6.common.Sup.Pair;
import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.common.models.MusicBand;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class Show extends Command {

    public Show(){
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении", new EmptyValidator());
    }

    @Override
    public ExecutionStatus execute(String argument, Pair<String, String> user) {
        if (collectionManager.getCollection().isEmpty()) {
            return new ExecutionStatus(true, "Коллекция пуста.\n");
        }
        return new ExecutionStatus(true, collectionManager.getCollection());
    }
}
