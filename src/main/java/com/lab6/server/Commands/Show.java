package com.lab6.server.Commands;

import com.lab6.server.managers.CollectionManager;
import models.MusicBand;
import sup.Console;
import sup.ExecutionStatus;

public class Show extends Command {
    private final CollectionManager collectionManager;

    public Show(Console console, CollectionManager collectionManager){
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении", console);
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionStatus execute(String arg){
        console.println("Вывод всех элементов коллекции:");
        if (collectionManager.getCollection().isEmpty()) {
            console.println("Коллекция пуста");
        }
        for (MusicBand band : collectionManager.getCollection()) {
            console.println(band.toString());
            console.println("");
        }
        return new ExecutionStatus(true, "Вывод всех элементов коллекции успешно завершен");
    }
}
