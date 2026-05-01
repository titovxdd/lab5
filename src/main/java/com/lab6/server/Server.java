package com.lab6.server;

import com.lab6.common.Sup.ExecutionStatus;
import com.lab6.server.managers.CollectionManager;
import com.lab6.server.managers.CommandManager;
import com.lab6.server.managers.DBManager;
import com.lab6.server.managers.ThreadManager;
import com.lab6.server.Commands.*;

import java.io.IOException;
import java.util.logging.*;

public final class Server {

    private static final int PORT = 6767;
    private static volatile boolean isRunning = true;

    private static CommandManager commandManager;
    private static ThreadManager threadManager;
    private static CollectionManager collectionManager;
    private static DBManager dbManager;

    public static final Logger logger = Logger.getLogger(Server.class.getName());

    static {
        initLogger();
    }

    private static void initLogger() {
        try {
            ConsoleHandler consoleHandler = new ConsoleHandler();
            FileHandler fileHandler = new FileHandler("server_logs.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            logger.setUseParentHandlers(false);
            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Failed to initialize file handler for logger: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // 1. Инициализация менеджеров
            logger.info("Initializing server components...");

            dbManager = DBManager.getInstance();
            collectionManager = CollectionManager.getInstance();
            commandManager = initCommandManager();
            threadManager = ThreadManager.getInstance();

            // 2. Загрузка коллекции из БД
            logger.info("Loading collection from database...");
            ExecutionStatus loadStatus = collectionManager.loadCollection();
            if (!loadStatus.isSuccess()) {
                logger.severe("Failed to load collection: " + loadStatus.getMessage());
                System.exit(1);
            }
            logger.info("Collection loaded successfully. Size: " + collectionManager.size());

            // 3. Добавление хука для graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Shutting down server...");
                isRunning = false;
                try {
                    threadManager.stopServer();
                } catch (Exception e) {
                    logger.severe("Error during shutdown: " + e.getMessage());
                }
                logger.info("Server stopped");
            }));

            // 4. Запуск сервера
            logger.info("Starting server on port " + PORT + "...");
            threadManager.startServer(commandManager, PORT);

        } catch (Exception e) {
            logger.severe("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static CommandManager initCommandManager() {
        CommandManager manager = new CommandManager();

        // Команды аутентификации (не требуют предварительного логина)
        manager.register("register", new Register());
        manager.register("login", new Login());

        // Основные команды
        manager.register("help", new Help(manager));
        manager.register("info", new Info());
        manager.register("show", new Show());
        manager.register("add", new Add());
        manager.register("head", new Head());
        manager.register("update", new Update());
        manager.register("remove_by_id", new RemoveById());
        manager.register("clear", new Clear());
        manager.register("add_if_min", new AddIfMin());
        manager.register("add_if_max", new AddIfMax());
        manager.register("count_less_than_description", new CountLessThanDescription());
        manager.register("filter_greater_than_genre", new FilterGreaterThanGenre());
        manager.register("filter_contains_name", new FilterContainsName());


        logger.info("Registered " + manager.getCommandsMap().size() + " commands");
        return manager;
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static void stopServer() {
        isRunning = false;
    }

    public static CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public static CommandManager getCommandManager() {
        return commandManager;
    }
}