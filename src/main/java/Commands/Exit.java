package Commands;

import sup.Console;
import sup.Status;

public class Exit extends Command{

    public Exit(Console console){
        super("exit", "завершить программу (без сохранения в файл)", console);
    }

    public Status execute(String arg){
        System.exit(0);
        return new Status(true, "Программа завершена!");
    }
}
