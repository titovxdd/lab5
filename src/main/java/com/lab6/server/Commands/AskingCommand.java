package com.lab6.server.Commands;

import managers.BandAsker;
import com.lab6.server.managers.CollectionManager;
import models.MusicBand;
import sup.Console;
import com.lab6.common.validators.IdValidator;
import sup.Pair;
import sup.ExecutionStatus;

public abstract class AskingCommand extends Command{
    protected final CollectionManager collectionManager;


    public AskingCommand(String name, String description, Console console, CollectionManager collectionManager){
        super(name, description, console);
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionStatus execute(String arg){
        Long id;
        IdValidator validator = new IdValidator(collectionManager);
        if (validator.validate(arg).isSuccess()) {
            id = Long.parseLong(arg);
            Pair<ExecutionStatus, MusicBand> validationStatusPair = parseBand(console, id);
            if (!validationStatusPair.getFirst().isSuccess()) {
                return validationStatusPair.getFirst();
            } else {
                return execute(validationStatusPair.getSecond());
            }
        } else {
            id = collectionManager.getFreeId();
            Pair<ExecutionStatus, MusicBand> validationStatusPair = parseBand(console, id);
            if (!validationStatusPair.getFirst().isSuccess()) {
                return validationStatusPair.getFirst();
            } else {
                return execute(validationStatusPair.getSecond());
            }

        }
    }

    public abstract ExecutionStatus execute(MusicBand band);


    public Pair<ExecutionStatus, MusicBand> parseBand(Console console, Long id){
        try {
            MusicBand band = BandAsker.askBand(console, id);
            if (band != null && band.validate()) {
                return new Pair<>(new ExecutionStatus(true, "Элемент введён корректно"), band);
            }
            return new Pair<>(new ExecutionStatus(false, "Введены некорректные данные"), null);
        } catch (BandAsker.Breaker e) {
            return new Pair<>(new ExecutionStatus(false, "Ввод был прерван пользователем"), null);
        } catch (BandAsker.IllegalInputException e) {
            return new Pair<>(new ExecutionStatus(false, e.getMessage()), null);
        }
    }

}
