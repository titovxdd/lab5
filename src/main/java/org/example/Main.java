package org.example;

import com.lab6.server.Commands.*;
import com.lab6.server.managers.Commands;
import com.lab6.server.managers.CollectionManager;
import com.lab6.server.managers.DumpManager;
import com.lab6.server.managers.Executer;
import sup.StandartConsole;
import sup.ExecutionStatus;


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
        CollectionManager collectionManager = new CollectionManager(dumpManager);
        ExecutionStatus loadStatus = collectionManager.loadCollection();


        if (!loadStatus.isSuccess()){
            console.printError(loadStatus.getMessage());
            System.exit(1);
        }

        Executer executer = new Executer(console);
        Commands commandManager = new Commands() {{
            register("help",new Help(console, this));
            register("info",new Info(console, collectionManager));
            register("show",new Show(console, collectionManager));
            register("add",new Add(console, collectionManager));
            register("exit", new Exit(console));
            register("head", new Head(console, collectionManager));
            register("add_if_min", new AddIfMin(console, collectionManager));
            register("add_if_max", new AddIfMax(console, collectionManager));
            register("count_less_than_description", new CountLessThanDescription(console, collectionManager));
            register("filter_greater_than_genre", new FilterGreaterThanGenre(console, collectionManager));
            register("remove_by_id", new RemoveById(console, collectionManager));
            register("update", new Update(console, collectionManager));
            register("save", new Save(console, collectionManager));
            register("clear", new Clear(console, collectionManager));
            register("filter_contains_name", new FilterContainsName(console, collectionManager));
            register("execute_script", new ExecuteScript(console, executer));
        }};
        executer.interactiveMode();
    }
}