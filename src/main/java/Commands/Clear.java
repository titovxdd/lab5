package Commands;

import managers.Context;
import sup.Console;
import sup.Status;

public class Clear extends Command {
    private final Context context;

    public Clear(Console console,Context context){
        super("clear", "очистить коллекцию", console);
        this.context = context;
    }

    @Override
    public Status execute(String arg){
        context.clear();
        return new Status(true, "Коллекция успешно очищена");
    }

    @Override
    public Status parse(String arg){
        return new Status(true, " ");
    }
}
