package Commands;

import managers.Context;
import sup.Console;
import sup.IdValidator;
import sup.Status;

public class RemoveById extends Command{
    private final Context context;

    public RemoveById(Console console, Context context){
        super("remove_by_id", "удалить элемент из коллекции по его id", console);
        this.context = context;
    }

    @Override
    public Status execute(String arg) {
        Long id = Long.parseLong(arg);
        context.removeById(id);
        return new Status(true, "Элемент успешно удален");
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
