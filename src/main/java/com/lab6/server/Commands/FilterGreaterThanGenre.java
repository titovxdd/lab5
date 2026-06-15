package com.lab6.server.Commands;

import com.lab6.common.Sup.Pair;
import com.lab6.common.validators.GenreValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.common.models.MusicBand;
import com.lab6.common.models.MusicGenre;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;

public class FilterGreaterThanGenre extends Command{

    public FilterGreaterThanGenre(){
        super("filter_greater_than_genre", "вывести элементы, значение поля genre которых больше заданного", new GenreValidator());
    }


    @Override
    public ExecutionStatus execute(String arg, Pair<String, String> user){
        int count = 0;
            StringBuilder s = new StringBuilder();
            for (MusicBand band : collectionManager.getCollection()) {
                if (band.getGenre().compareTo(MusicGenre.valueOf(arg))>0){
                    s.append(band.toString());
                    count++;
                }
            }
            if (count == 0){
                return new ExecutionStatus(true, "Не существует элементов значение поля genre которых больше заданного");
            } else {
                s.append("Элементы успешно выведены");
                return new ExecutionStatus(true, s.toString());
            }
    }
}
