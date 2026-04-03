package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import models.MusicBand;
import models.MusicGenre;
import sup.Console;
import sup.GenreValidator;
import sup.ExecutionStatus;

public class FilterGreaterThanGenre extends Command{
    private final CollectionManager collectionManager;

    public FilterGreaterThanGenre(Console console, CollectionManager collectionManager){
        super("filter_greater_than_genre", "вывести элементы, значение поля genre которых больше заданного", console);
        this.collectionManager = collectionManager;
    }

    GenreValidator validator = new GenreValidator();

    @Override
    public ExecutionStatus execute(String arg){
        int count = 0;
        if (validator.validate(arg).isSuccess()) {
            for (MusicBand band : collectionManager.getCollection()) {
                if (band.getGenre().compareTo(MusicGenre.valueOf(arg))>0){
                    console.println(band.toString());
                    count++;
                }
            }
            if (count == 0){
                return new ExecutionStatus(true, "Не существует элементов значение поля genre которых больше заданного");
            } else {
                return new ExecutionStatus(true, "Элементы успешно выведены");
            }
        } else {
            return validator.validate(arg);
        }
    }
}
