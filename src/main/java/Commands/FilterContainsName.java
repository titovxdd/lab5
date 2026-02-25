package Commands;

import managers.Context;
import models.MusicBand;
import models.MusicGenre;
import sup.Console;
import sup.Status;

public class FilterContainsName extends Command{
    private final Context context;

    public FilterContainsName(Console console, Context context){
        super("filter_contains_name", "вывести элементы, значение поля name которых содержит заданную подстроку", console);
        this.context = context;
    }

    @Override
    public Status execute(String arg){
        int count = 0;
        if (!(arg.isEmpty())) {
            for (MusicBand band : context.getCollection()) {
                if (band.getName().contains(arg)){
                    console.println(band.toString());
                    count++;
                }
            }
            if (count == 0){
                return new Status(true, "Не существует элементов значение поля name содержит заданную подстроку");
            } else {
                return new Status(true, "Элементы успешно выведены");
            }
        } else {
            return new Status(false, "Команда должна иметь аргумент(name)");
        }
    }
}
