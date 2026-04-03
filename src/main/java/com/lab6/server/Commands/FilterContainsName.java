package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import models.MusicBand;
import sup.Console;
import sup.ExecutionStatus;

public class FilterContainsName extends Command{
    private final CollectionManager collectionManager;

    public FilterContainsName(Console console, CollectionManager collectionManager){
        super("filter_contains_name", "вывести элементы, значение поля name которых содержит заданную подстроку", console);
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionStatus execute(String arg){
        int count = 0;
        if (!(arg.isEmpty())) {
            for (MusicBand band : collectionManager.getCollection()) {
                if (band.getName().contains(arg)){
                    console.println(band.toString());
                    count++;
                }
            }
            if (count == 0){
                return new ExecutionStatus(true, "Не существует элементов значение поля name содержит заданную подстроку");
            } else {
                return new ExecutionStatus(true, "Элементы успешно выведены");
            }
        } else {
            return new ExecutionStatus(false, "Команда должна иметь аргумент(name)");
        }
    }
}
