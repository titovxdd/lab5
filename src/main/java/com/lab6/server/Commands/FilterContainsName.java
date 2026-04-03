package com.lab6.server.Commands;

import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.common.models.MusicBand;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class FilterContainsName extends Command{

    public FilterContainsName(){
        super("filter_contains_name", "вывести элементы, значение поля name которых содержит заданную подстроку", new EmptyValidator());
    }

    @Override
    public ExecutionStatus execute(String arg){
        int count = 0;
        StringBuilder s = new StringBuilder();
            for (MusicBand band : collectionManager.getCollection()) {
                if (band.getName().contains(arg)){
                    s.append(band.toString());
                    count++;
                }
            }
            if (count == 0){
                return new ExecutionStatus(true, "Не существует элементов значение поля name содержит заданную подстроку");
            } else {
                return new ExecutionStatus(true, s.toString());
            }
        }
}
