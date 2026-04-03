package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import models.MusicBand;
import sup.Console;
import sup.ExecutionStatus;

public class CountLessThanDescription extends Command{
    private final CollectionManager collectionManager;

    public CountLessThanDescription(Console console, CollectionManager collectionManager){
        super("count_less_than_description", "вывести количество элементов, значение поля description которых меньше заданного", console);
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionStatus execute(String arg){
        int count = 0;
        for (MusicBand band : collectionManager.getCollection()){
            if (band.getDescription().compareTo(arg)<0){
                count++;
            }
        }
        console.println("Количество элементов, значение поля description которых меньше заданного: "+ count);
        return new ExecutionStatus(true, "Команда успешно выполнена");
    }
}
