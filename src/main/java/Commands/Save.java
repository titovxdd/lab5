package Commands;

import managers.Context;
import sup.Console;
import sup.Status;

public class Save extends Command{
    private final Context context;

    public Save(Console console, Context context){
        super("save", "сохранить коллекцию в файл", console);
        this.context = context;
    }

    @Override
    public Status execute(String arg) {
        context.saveCollection();
        return new Status(true, "Коллекция успешно сохранена");
    }
}
