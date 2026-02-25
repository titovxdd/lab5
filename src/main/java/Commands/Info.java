package Commands;

import managers.Context;
import sup.Console;
import sup.Status;

public class Info extends Command{
    private final Context context;

    public Info(Console console, Context context){
        super("info", "вывести в стандартный поток вывода информацию о коллекции", console);
        this.context = context;
    }

    @Override
    public Status parse(String arg){
        return new Status(true, " ");
    }

    @Override
    public Status execute(String arg){
        console.println("Тип коллекции: " + context.getCollection().getClass().getName());
        console.println("Дата инициализации: " + context.getInitializationDate());
        console.println("Дата последнего сохранения: " + context.getLastSaveDate());
        console.println("Количество элементов: " + context.getCollection().size());
        return new Status(true, "Информация о коллекции успешно выведена");
    }
}
