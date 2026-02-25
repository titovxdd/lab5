package Commands;

import managers.Context;
import models.MusicBand;
import sup.Console;
import sup.Status;

public class AddIfMin extends AskingCommand{
    public AddIfMin(Console console, Context context){
        super("add_if_min", "добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции", console, context);
    }

    @Override
    public Status execute(MusicBand band){
        if (context.getCollection().isEmpty()) {
            context.add(band);
            return new Status(true, "Коллекция пуста. Элемент добавлен как наименьший");
        }
        if (band.compareTo(context.head())< 0){
            context.add(band);
            return new Status(true, "Элемент успешно добавлен в коллекцию");
        } else {
            return new Status(true, "Элемент не является наименьшим в коллекции");
        }
    }
}
