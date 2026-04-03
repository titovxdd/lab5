package com.lab6.server;

import com.lab6.common.Sup.Request;
import com.lab6.common.Sup.Response;
import com.lab6.server.Commands.*;
import com.lab6.server.managers.CollectionManager;
import com.lab6.server.managers.CommandManager;
import com.lab6.server.managers.Executer;
import com.lab6.server.managers.ServerNetworkManager;
import com.lab6.common.Sup.ExecutionStatus;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.*;

public final class Server {
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


    private static final int PORT = 6767;
    private static CommandManager commandManager;
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

        commandManager = new CommandManager() {{
            register("help",new Help(this));
            register("info",new Info());
            register("show",new Show());
            register("add",new Add());
            register("head", new Head());
            register("add_if_min", new AddIfMin());
            register("add_if_max", new AddIfMax());
            register("count_less_than_description", new CountLessThanDescription());
            register("filter_greater_than_genre", new FilterGreaterThanGenre());
            register("remove_by_id", new RemoveById());
            register("update", new Update());
            register("clear", new Clear());
            register("filter_contains_name", new FilterContainsName());
        }};

        Executer executer = new Executer(commandManager);

        run(executer);
    }

    public static void run(Executer executer) {
        try {
            networkManager.startServer();
            logger.info("Server started");

            while (isRunning) {
                Socket clientSocket = null;
                try {
                    clientSocket = networkManager.acceptConnection();


                    boolean clientConnected = true;
                    while (clientConnected && isRunning) {
                        try {
                            Request request = networkManager.receive(clientSocket);


                            String[] commandParts = request.getCommand();
                            ExecutionStatus executionStatus = executer.runCommand(commandParts, request.getBand());

                            Response response = new Response(executionStatus);

                            if (!executionStatus.isSuccess()) {
                                logger.severe(executionStatus.getMessage());
                            }

                            networkManager.send(response, clientSocket);

                        } catch (IOException e) {
                            logger.info("Client disconnected: " + e.getMessage());
                            clientConnected = false;
                        } catch (ClassNotFoundException e) {
                            logger.severe("Error deserializing request: " + e.getMessage());
                        }
                    }

                } catch (IOException e) {
                    if (isRunning) {
                        logger.severe("Error accepting client connection: " + e.getMessage());
                    }
                } finally {
                    if (clientSocket != null) {
                        networkManager.closeConnection(clientSocket);
                    }
                }
            }

        } catch (IOException e) {
            logger.severe("Error while running the server: " + e.getMessage());
        }
    }

}
