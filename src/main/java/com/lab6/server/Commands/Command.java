package com.lab6.server.Commands;

import sup.ArgParser;
import sup.Console;
import sup.Pair;
import sup.ExecutionStatus;

public abstract class Command implements ArgParser {
    private final Pair<String, String> nameAndDescription;
    protected Console console;

    public Command(String name, String description, Console console) {
        this.nameAndDescription = new Pair<>(name, description);
        this.console = console;
    }
    public ExecutionStatus parse(String arg){
        return new ExecutionStatus(true, "Аргумент валиден");
    }

    public void updateConsole(Console console) {
        this.console = console;
    }

    public String getName() {
        return nameAndDescription.getFirst();
    }

    public String getDescription() {
        return nameAndDescription.getSecond();
    }
    public abstract ExecutionStatus execute(String arg);

    @Override
    public int hashCode() {
        return nameAndDescription.getFirst().hashCode() + nameAndDescription.getSecond().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Command command = (Command) object;
        return nameAndDescription.getFirst().equals(command.nameAndDescription.getFirst()) &&
                nameAndDescription.getSecond().equals(command.nameAndDescription.getSecond());
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + nameAndDescription.getFirst() + '\'' +
                ", description='" + nameAndDescription.getSecond() + '\'' +
                '}';
    }
}