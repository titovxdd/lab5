package sup;

import models.MusicGenre;

public class GenreValidator {

    public Status validate(String arg){
        if (arg.isEmpty()) {
            return new Status(false, "У команды должен быть аргумент (genre)!");
        }
        try {
            MusicGenre genre = MusicGenre.valueOf(arg);
            return new Status(true, "Аргумент команды введен корректно.");
        } catch (IllegalArgumentException e) {
            return new Status(false, "Некорректное значение поля genre!\nСписок возможных значений: " + MusicGenre.list());
        }
    }
}
