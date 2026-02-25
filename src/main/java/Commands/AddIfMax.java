package Commands;

import managers.Context;
import models.MusicBand;
import sup.Console;
import sup.Status;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AddIfMax extends AskingCommand{

    public AddIfMax(Console console, Context context){
        super("add_if_max", "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции", console, context);
    }

    public Status execute(MusicBand band){
        if (context.getCollection().isEmpty()) {
            context.add(band);
            return new Status(true, "Коллекция пуста. Элемент добавлен как наибольший");
        }
        MusicBand maxBand = null;
        for (MusicBand b : context.getCollection()) {
            if (maxBand == null || b.getId() > maxBand.getId()) {
                maxBand = b;
            }
        }
        if (band.compareTo(maxBand) > 0){
            context.add(band);
            return new Status(true, "Элемент успешно добавлен в коллекцию");
        } else {
            return new Status(true, "Элемент не является наибольшим в коллекции");
        }
    }
}
