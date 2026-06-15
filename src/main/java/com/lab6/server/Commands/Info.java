package com.lab6.server.Commands;

import com.lab6.common.Sup.Pair;
import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class Info extends Command{

    public Info(){
        super("info", "вывести в стандартный поток вывода информацию о коллекции", new EmptyValidator());
    }


    @Override
    public ExecutionStatus execute(String argument, Pair<String, String> user) {
        String infoMessage = "Тип коллекции: java.util.PriorityQueue" +
                "\nДата инициализации: " + collectionManager.getInitializationDate() +
                "\nДата последнего сохранения: " + collectionManager.getLastSaveDate() +
                "\nКоличество элементов: " + collectionManager.getBands().size() +
                "\nИнформация о коллекции успешно выведена!";
        return new ExecutionStatus(true, infoMessage);
    }
}
