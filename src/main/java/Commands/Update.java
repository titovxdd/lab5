package Commands;

import managers.Context;
import models.MusicBand;
import sup.Console;
import sup.IdValidator;
import sup.Status;

public class Update extends AskingCommand{

    public Update(Console console, Context context){
        super("update", "обновить значение элемента коллекции, id которого равен заданному", console, context);
    }

    @Override
    public Status execute(MusicBand band){
        context.removeById(band.getId());
        context.add(band);
        return new Status(true, "Элемент успешно обновлён!");
    }

    @Override
    public Status parse(String arg){
        IdValidator validator = new IdValidator(context);
        if (validator.validate(arg).isSuccess()){
            return new Status(true, "ID валидно");
        } else {
            return validator.validate(arg);
        }
    }
}
