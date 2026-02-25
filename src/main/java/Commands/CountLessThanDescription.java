package Commands;

import managers.Context;
import models.MusicBand;
import sup.Console;
import sup.Status;

public class CountLessThanDescription extends Command{
    private final Context context;

    public CountLessThanDescription(Console console, Context context){
        super("count_less_than_description", "вывести количество элементов, значение поля description которых меньше заданного", console);
        this.context = context;
    }

    @Override
    public Status execute(String arg){
        int count = 0;
        for (MusicBand band : context.getCollection()){
            if (band.getDescription().compareTo(arg)<0){
                count++;
            }
        }
        console.println("Количество элементов, значение поля description которых меньше заданного: "+ count);
        return new Status(true, "Команда успешно выполнена");
    }
}
