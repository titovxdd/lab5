package org.example;

import Commands.*;
import managers.Commands;
import managers.Context;
import managers.DumpManager;
import managers.Executer;
import sup.IdValidator;
import sup.StandartConsole;
import sup.Status;


public class Main {
    public static void main(String[] args) {
        var console = new StandartConsole();
        String filePath = System.getenv("LAB5_FILE_PATH");

        if (filePath == null) {
            console.printError("Переменная окружения LAB5_FILE_PATH не найдена");
            System.exit(1);
        } else if (filePath.isEmpty()) {
            console.printError("Переменная окружения LAB5_FILE_PATH не содержит пути к файлу");
            System.exit(1);
        } else if (!filePath.endsWith(".json")) {
            console.printError("Файл должен быть формата .json");
            System.exit(1);
        } else if (!new java.io.File(filePath).exists()) {
            console.printError("Файл по указанному пути не найден");
            System.exit(1);
        }
        DumpManager dumpManager = new DumpManager(filePath, console);
        Context context = new Context(dumpManager);
        Status loadStatus = context.loadCollection();


        if (!loadStatus.isSuccess()){
            console.printError(loadStatus.getMessage());
            System.exit(1);
        }

        Commands commandManager = new Commands() {{
            register("help",new Help(console, this));
            register("info",new Info(console, context));
            register("show",new Show(console, context));
            register("add",new Add(console, context));
            register("exit", new Exit(console));
            register("head", new Head(console, context));
            register("add_if_min", new AddIfMin(console, context));
            register("add_if_max", new AddIfMax(console, context));
            register("count_less_than_description", new CountLessThanDescription(console, context));
            register("filter_greater_than_genre", new FilterGreaterThanGenre(console, context));
            register("remove_by_id", new RemoveById(console, context));
            register("update", new Update(console, context));
            register("save", new Save(console, context));
            register("clear", new Clear(console, context));
            register("filter_contains_name", new FilterContainsName(console, context));
        }};
        new Executer(console).interactiveMode();
    }
}