package com.lab6.server.Commands;

import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class Head extends Command{

    public Head(){
        super("head", "вывести первый элемент коллекции", new EmptyValidator());
    }

    public ExecutionStatus execute(String arg){
        if (collectionManager.getCollection().isEmpty()){
            return new ExecutionStatus(false, "Коллекция пуста");
        }
        return new ExecutionStatus(true, collectionManager.head().toString() +"\nВыведен первый элемент коллекции");
    }
}
