package Commands;

import managers.Context;
import models.MusicBand;
import models.MusicGenre;
import sup.Console;
import sup.GenreValidator;
import sup.Status;

public class FilterGreaterThanGenre extends Command{
    private final Context context;

    public FilterGreaterThanGenre(Console console, Context context){
        super("filter_greater_than_genre", "вывести элементы, значение поля genre которых больше заданного", console);
        this.context = context;
    }

    GenreValidator validator = new GenreValidator();

    @Override
    public Status execute(String arg){
        int count = 0;
        if (validator.validate(arg).isSuccess()) {
            for (MusicBand band : context.getCollection()) {
                if (band.getGenre().compareTo(MusicGenre.valueOf(arg))>0){
                    console.println(band.toString());
                    count++;
                }
            }
            if (count == 0){
                return new Status(true, "Не существует элементов значение поля genre которых больше заданного");
            } else {
                return new Status(true, "Элементы успешно выведены");
            }
        } else {
            return validator.validate(arg);
        }
    }
}
