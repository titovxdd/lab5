package com.lab6.server;

import com.lab6.common.Sup.Request;
import com.lab6.common.Sup.Response;
import com.lab6.common.validators.ArgumentValidator;
import com.lab6.server.Commands.*;
import com.lab6.server.managers.CollectionManager;
import com.lab6.server.managers.Commands;
import com.lab6.server.managers.Executer;
import com.lab6.server.managers.ServerNetworkManager;
import sup.Pair;
import sup.ExecutionStatus;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

public final class Server {
    public static final Logger logger = Logger.getLogger(Server.class.getName());

    static {
        initLogger();
    }

    private static void initLogger() {
        try {
            ConsoleHandler consoleHandler = getConsoleHandler();
            FileHandler fileHandler = new FileHandler("server_logs.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            logger.setUseParentHandlers(false);
            logger.addHandler(consoleHandler);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Failed to initialize file handler for logger: " + e.getMessage());
        }
    }

    private static ConsoleHandler getConsoleHandler() {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                String color = switch (record.getLevel().getName()) {
                    case "SEVERE" -> "\u001B[31m";
                    case "WARNING" -> "\u001B[33m";
                    case "INFO" -> "\u001B[32m";
                    default -> "\u001B[0m";
                };
                return color + "[" + record.getLevel() + "] " +
                        "[" + Thread.currentThread().getName() + "] " +
                        "[" + new java.util.Date(record.getMillis()) + "] " +
                        formatMessage(record) + "\u001B[0m\n";
            }
        });
        return consoleHandler;
    }

    private static final int PORT = 13876;
    private static Commands commandManager;
    private static ServerNetworkManager networkManager;
    private static final CollectionManager collectionManager = CollectionManager.getInstance();
    private static volatile boolean isRunning = true;

    public static void main(String[] args) {
        ExecutionStatus loadStatus = collectionManager.loadCollection();
        networkManager = new ServerNetworkManager(PORT);

        if (!loadStatus.isSuccess()) {
            logger.severe(loadStatus.getMessage());
            System.exit(1);
        }
        logger.info("The collection file has been successfully loaded!");

        // Регистрация команд
        commandManager = new Commands() {{
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

        Executer executer = new Executer(commandManager);

        // Хук для завершения работы
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                isRunning = false;
                collectionManager.saveCollection();
                networkManager.stopServer();
                logger.info("Server shutdown complete.");
            } catch (Exception e) {
                logger.severe("An error occurred while shutting down the server: " + e.getMessage());
            } finally {
                for (var handler : logger.getHandlers()) {
                    handler.close();
                }
            }
        }));

        run(executer);
    }

    public static void run(Executer executer) {
        try {
            networkManager.startServer();
            logger.info("Server started on port " + PORT);
            logger.info("To stop the server, press [Ctrl + C]");

            while (isRunning) {
                try {
                    // БЛОКИРУЮЩИЙ вызов - ожидание подключения клиента
                    Socket clientSocket = networkManager.acceptConnection();

                    // Отправка списка команд новому клиенту
                    sendCommandsToClient(clientSocket);

                    // Цикл обработки запросов от текущего клиента
                    boolean clientConnected = true;
                    while (clientConnected && isRunning) {
                        try {
                            // БЛОКИРУЮЩЕЕ чтение запроса от клиента
                            Request request = networkManager.receive(clientSocket);

                            logger.info("Request received from client: " + request);

                            // Выполнение команды
                            ExecutionStatus executionStatus = executer.runCommand(
                                    request.getCommand(),
                                    request.getBand()
                            );

                            Response response = new Response(executionStatus);

                            if (!executionStatus.isSuccess()) {
                                logger.severe(executionStatus.getMessage());
                            } else {
                                logger.info("Command executed successfully");
                            }

                            // Отправка ответа клиенту
                            networkManager.send(response, clientSocket);

                        } catch (SocketException | IOException e) {
                            logger.info("Client " + clientSocket.getInetAddress() + " disconnected: " + e.getMessage());
                            clientConnected = false;
                        } catch (ClassNotFoundException e) {
                            logger.severe("Error deserializing request: " + e.getMessage());
                        }
                    }

                    // Закрытие соединения с текущим клиентом
                    networkManager.closeConnection(clientSocket);

                } catch (IOException e) {
                    if (isRunning) {
                        logger.severe("Error accepting client connection: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            logger.severe("Error while running the server: " + e.getMessage());
        }
    }

    private static void sendCommandsToClient(Socket clientSocket) {
        try {
            Map<String, Pair<ArgumentValidator, Boolean>> commandsData = new HashMap<>();
            for (Map.Entry<String, Command> entry : commandManager.getCommandsMap().entrySet()) {
                boolean isAskingCommand = AskingCommand.class.isAssignableFrom(entry.getValue().getClass());
                commandsData.put(entry.getKey(), new Pair<>(entry.getValue().getArgumentValidator(), isAskingCommand));
            }

            Response commandsResponse = new Response(commandsData);
            networkManager.send(commandsResponse, clientSocket);
            logger.info("Command list sent to the client: " + clientSocket.getInetAddress());

        } catch (IOException e) {
            logger.severe("Error sending command list to client: " + e.getMessage());
        }
    }
}
