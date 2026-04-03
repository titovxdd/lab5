package com.lab6.server.Commands;

import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.common.models.MusicBand;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class CountLessThanDescription extends Command{

    public CountLessThanDescription(){
        super("count_less_than_description", "вывести количество элементов, значение поля description которых меньше заданного", new EmptyValidator());
    }

    @Override
    public ExecutionStatus execute(String arg){
        int count = 0;
        for (MusicBand band : collectionManager.getCollection()){
            if (band.getDescription().compareTo(arg)<0){
                count++;
            }
        }
        return new ExecutionStatus(true, "Количество элементов, значение поля description которых меньше заданного: "+ count);
    }
}
