package com.lab6.common.Sup;

import sup.ExecutionStatus;

import java.io.Serial;
import java.io.Serializable;

public class Response implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
    private Map<String, Pair<, Boolean>> commandsData; // Второе значение - true, если команда требует ввода элемента коллекции
    private ExecutionStatus executionStatus;

    public Response(Map<String, Pair<, Boolean>> commandsData) {
        this.commandsData = commandsData;
    }

    public Response(ExecutionStatus executionStatus) {
        this.executionStatus = executionStatus;
    }

    public ExecutionStatus getExecutionStatus() {
        return executionStatus;
    }

    public Map<String, Pair<, Boolean>> getCommandsMap() {
        return commandsData;
    }

    @Override
    public String toString() {
        return "Response{" +
                "commandsData=" + commandsData +
                ", executionStatus='" + executionStatus + '\'' +
                '}';
    }
}
