package com.lab6.common.validators;

import com.lab6.common.Sup.ExecutionStatus;

import java.io.Serial;
import java.io.Serializable;

public class EmptyValidator extends ArgumentValidator implements Serializable {
    @Serial
    private static final long serialVersionUID = 106L;

    @Override
    public ExecutionStatus validate(String arg, String name) {
        if (!arg.isEmpty()) {
            return new ExecutionStatus(false, "У команды нет аргументов");
        }
        return new ExecutionStatus(true, "Аргумент команды введен корректно.");
    }
}