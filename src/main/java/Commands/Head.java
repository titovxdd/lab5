package Commands;

import managers.Context;
import sup.Console;
import sup.Status;

public class Head extends Command{
    private final Context context;

    public Head(Console console, Context context){
        super("head", "вывести первый элемент коллекции", console);
        this.context = context;
    }

    public Status execute(String arg){
        if (context.getCollection().isEmpty()){
            return new Status(false, "Коллекция пуста");
        }
        console.println(context.head().toString());
        return new Status(true, "Выведен первый элемент коллекции");
    }
}
