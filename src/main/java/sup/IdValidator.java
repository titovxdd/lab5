package sup;

import managers.Context;

public class IdValidator {
    private final Context context;

    public IdValidator(Context context) {
        this.context = context;
    }

    public Status validate(String arg){
        if (arg.isEmpty()) {
            return new Status(false, "У команды должен быть аргумент (id элемента коллекции)");
        }
        try {
            Long id = Long.parseLong(arg);
            if (context.getById(id) == null) {
                return new Status(false, "Элемент с указанным id не найден");
            }
        } catch (NumberFormatException e) {
            return new Status(false, "Формат аргумента неверен Он должен быть целым числом.");
        }
        return new Status(true, "Аргумент команды введен корректно.");
    }
}
