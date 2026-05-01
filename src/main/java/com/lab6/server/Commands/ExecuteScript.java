package com.lab6.server.Commands;

import com.lab6.common.Sup.ExecutionStatus;
import com.lab6.common.Sup.Pair;
import com.lab6.common.validators.EmptyValidator;

public class ExecuteScript extends Command{

    public ExecuteScript() {
        super("execute_script", "позволяет выполнить команды из файла", new EmptyValidator());
    }

    @Override
    public ExecutionStatus execute(String arg, Pair<String, String> user) {
        return new ExecutionStatus(true, "Данную команду нельзя отправить на сервер");
    }
}
