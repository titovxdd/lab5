package Commands;

import managers.Context;
import models.MusicBand;
import sup.Console;
import sup.Status;

public class Add extends AskingCommand{

    public Add(Console console, Context context){
        super("add", "добавить новый элемент в коллекцию", console, context);
    }

    @Override
    public Status execute(MusicBand band) {
        context.add(band);
        return new Status(true, "Элемент успешно добавлен в коллекцию");
    }
}
