package com.lab6.common.validators;

import com.lab6.common.models.MusicGenre;
import com.lab6.common.Sup.ExecutionStatus;

public class GenreValidator extends ArgumentValidator{

    @Override
    public ExecutionStatus validate(String arg) {
        if (arg.isEmpty()) {
            return new ExecutionStatus(false, "У команды должен быть аргумент (genre)");
        }
        try {
            MusicGenre genre = MusicGenre.valueOf(arg);
            return new ExecutionStatus(true, "Аргумент команды введен корректно.");
        } catch (IllegalArgumentException e) {
            return new ExecutionStatus(false, "Некорректное значение поля genre\nСписок возможных значений: " + MusicGenre.list());
        }
    }
}
