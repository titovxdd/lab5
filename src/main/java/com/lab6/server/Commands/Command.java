package com.lab6.server.Commands;

import com.lab6.common.validators.ArgumentValidator;
import com.lab6.server.managers.CollectionManager;
import com.lab6.common.Sup.Pair;
import com.lab6.common.Sup.ExecutionStatus;

public abstract class Command {
    private final Pair<String, String> nameAndDescription;
    protected static final CollectionManager collectionManager = CollectionManager.getInstance();
    private final ArgumentValidator argumentValidator;


    public Command(String name, String description, ArgumentValidator argumentValidator) {
        this.nameAndDescription = new Pair<>(name, description);
        this.argumentValidator = argumentValidator;
    }

    public ArgumentValidator getArgumentValidator() {
        return argumentValidator;
    }

    public ExecutionStatus run(String arg) {
        ExecutionStatus argumentStatus = argumentValidator.validate(arg);
        if (argumentStatus.isSuccess()) {
            return execute(arg);
        } else {
            return argumentStatus;
        }
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