package com.lab6.common.validators;

import com.lab6.common.Sup.ExecutionStatus;

public class EmptyValidator extends ArgumentValidator{

    @Override
    public ExecutionStatus validate(String arg) {
        if (!arg.isEmpty()) {
            return new ExecutionStatus(false, "У команды нет аргументов");
        }
        return new ExecutionStatus(true, "Аргумент команды введен корректно.");
    }
}