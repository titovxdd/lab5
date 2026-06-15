package com.lab6.common.validators;

import com.lab6.common.Sup.ExecutionStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class IdValidator extends ArgumentValidator implements Serializable {
    @Serial
    private static final long serialVersionUID = 109L;

    @Override
    public ExecutionStatus validate(String arg, String name) {
        arg = arg.trim();

        if (arg.isEmpty()) {
            return new ExecutionStatus(false, "У команды должен быть аргумент (id элемента коллекции)");
        }
        try {
            Long id = Long.parseLong(arg);
        } catch (NumberFormatException e) {
            return new ExecutionStatus(false, "Формат аргумента неверен Он должен быть целым числом.");
        }
        return new ExecutionStatus(true, "Аргумент команды введен корректно.");
    }
}
