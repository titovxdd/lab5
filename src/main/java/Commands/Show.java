package Commands;

import managers.Context;
import models.MusicBand;
import sup.Console;
import sup.Status;

public class Show extends Command {
    private final Context context;

    public Show(Console console, Context context){
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении", console);
        this.context = context;
    }

    @Override
    public Status execute(String arg){
        console.println("Вывод всех элементов коллекции:");
        if (context.getCollection().isEmpty()) {
            console.println("Коллекция пуста");
        }
        for (MusicBand band : context.getCollection()) {
            console.println(band.toString());
            console.println("");
        }
        return new Status(true, "Вывод всех элементов коллекции успешно завершен");
    }
}
