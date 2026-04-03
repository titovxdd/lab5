package com.lab6.server.Commands;

import sup.Console;
import sup.ExecutionStatus;

public class Exit extends Command{

    public Exit(Console console){
        super("exit", "завершить программу (без сохранения в файл)", console);
    }

    public ExecutionStatus execute(String arg){
        System.exit(0);
        return new ExecutionStatus(true, "Программа завершена!");
    }
}
