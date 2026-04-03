package com.lab6.common.validators;

import sup.ExecutionStatus;

public class IdValidator extends ArgumentValidator {

    @Override
    public ExecutionStatus validate(String arg) {
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
