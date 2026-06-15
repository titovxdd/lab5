package com.lab6.client;

import com.lab6.client.managers.ClientNetworkManager;
import com.lab6.client.sup.FileConsole;
import com.lab6.client.sup.StandartConsole;
import com.lab6.common.Sup.ExecutionStatus;
import com.lab6.common.Sup.Pair;
import com.lab6.common.Sup.Request;
import com.lab6.common.Sup.Response;
import com.lab6.common.validators.ArgumentValidator;
import com.lab6.common.validators.ElementValidator;
import com.lab6.common.models.MusicBand;
import com.lab6.client.sup.Console;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

public final class Client {
    private static final Console console = new StandartConsole();
    private static final int SERVER_PORT = 6767;
    private static final String SERVER_HOST = System.getenv().getOrDefault("SERVER_HOST", "localhost");

    private static Map<String, Pair<ArgumentValidator, Boolean>> commandsData;
    private static final ClientNetworkManager networkManager = new ClientNetworkManager(SERVER_PORT, SERVER_HOST);
    private static Pair<String, String> currentUser = null;
    private static int connectionAttempts = 1;
    private static int scriptStackCounter = 0;

    public static void main(String[] args) {
        do {
            try {
                
                networkManager.connect();
                console.println("Подключено к " + SERVER_HOST + ":" + SERVER_PORT);

                
                Response commandsResponse = networkManager.receive();
                commandsData = commandsResponse.getCommandsMap();
                console.println("Список команд получен от сервера");

                
                currentUser = authenticateUser();
                if (currentUser == null) {
                    console.printError("Не удалось пройти аутентификацию");
                    networkManager.close();
                    continue;
                }
                console.println("Аутентификация успешна! Добро пожаловать, " + currentUser.getFirst());

                connectionAttempts = 1;

                
                while (true) {
                    console.println("\nВведите команду:");
                    String inputCommand = console.readln().trim();

                    if (inputCommand.equals("exit")) {
                        console.println("Завершение работы клиента");
                        networkManager.close();
                        System.exit(0);
                    }

                    ExecutionStatus argumentStatus = validateCommand((inputCommand + " ").split(" ", 2));
                    if (!argumentStatus.isSuccess()) {
                        console.printError(argumentStatus.getMessage());
                        continue;
                    }

                    Request request = prepareRequest(console, inputCommand);
                    if (request == null) {
                        continue;
                    }

                    networkManager.send(request);
                    Response response = networkManager.receive();

                    if (response.getExecutionStatus().isSuccess()) {
                        if (response.getExecutionStatus().getMessage() == null) {
                            response.getExecutionStatus().getCollection()
                                    .forEach(item -> console.println(item.toString()));
                        } else {
                            console.println(response.getExecutionStatus().getMessage());
                        }
                    } else {
                        console.printError(response.getExecutionStatus().getMessage());
                    }
                }

            } catch (IOException e) {
                console.printError("Не удалось подключиться к серверу. Попытка " + connectionAttempts + "/5");
                try {
                    Thread.sleep(2000);
                    connectionAttempts++;
                } catch (InterruptedException ignored) {}
            } catch (ClassNotFoundException e) {
                console.printError("Ошибка при работе с сервером: " + e.getMessage());
            }
        } while (connectionAttempts <= 5);

        console.printError("Превышено максимальное количество попыток подключения к серверу.");
    }

    public static Pair<String, String> getCurrentUser() {
        return currentUser;
    }
    
    private static Pair<String, String> authenticateUser() throws IOException, ClassNotFoundException {
        while (true) {
            console.println("\n=== АУТЕНТИФИКАЦИЯ ===");
            console.println("Введите 'register' для регистрации или 'login' для входа:");
            String command = console.readln().trim().toLowerCase();

            if (command.equals("exit")) {
                console.println("Завершение работы клиента");
                networkManager.close();
                System.exit(0);
            }
            if (!command.equals("register") && !command.equals("login")) {
                console.printError("Введите 'register' или 'login'");
                continue;
            }

            console.println("Введите логин:");
            String username = console.readln().trim();

            console.println("Введите пароль:");
            String password = console.readln().trim();

            
            Request request = new Request(command, new Pair<>(username, password));
            networkManager.send(request);

            Response response = networkManager.receive();

            if (response.getExecutionStatus().isSuccess()) {
                console.println(response.getExecutionStatus().getMessage());
                return new Pair<>(username, password);
            } else {
                console.printError(response.getExecutionStatus().getMessage());
            }
        }
    }

    
    private static Request askingRequest(Console console, String inputCommand, Pair<String, String> user) {
        ElementValidator elementValidator = new ElementValidator();
        Pair<ExecutionStatus, MusicBand> validationStatusPair = elementValidator.validateAsking(console);

        if (!validationStatusPair.getFirst().isSuccess()) {
            console.printError(validationStatusPair.getFirst().getMessage());
            return null;
        } else {
            
            return new Request(inputCommand, validationStatusPair.getSecond(), user);
        }
    }

    
    private static Request prepareRequest(Console console, String inputCommand) {
        String[] commands = (inputCommand.trim() + " ").split(" ", 2);

        if (commandsData == null || !commandsData.containsKey(commands[0])) {
            console.printError("Команда не найдена");
            return null;
        }

        Pair<ArgumentValidator, Boolean> commandInfo = commandsData.get(commands[0]);

        if (commandInfo.getSecond()) {
            
            return askingRequest(console, inputCommand, currentUser);
        } else if (commands[0].equals("execute_script")) {
            if (commands[1].isEmpty()) {
                console.printError("Укажите имя файла скрипта. Пример: execute_script script.txt");
                return null;
            }
            ExecutionStatus scriptStatus = runScript(commands[1].trim());
            if (!scriptStatus.isSuccess()) {
                console.printError(scriptStatus.getMessage());
                return null;
            }
            return null;
        } else {
            
            return new Request(inputCommand, currentUser);
        }
    }

    
    private static ExecutionStatus runScript(String fileName) {
        try {
            scriptStackCounter++;
            if (scriptStackCounter > 5) {
                scriptStackCounter--;
                return new ExecutionStatus(false, "Превышена максимальная глубина рекурсии!");
            }

            if (fileName.isEmpty()) {
                scriptStackCounter--;
                return new ExecutionStatus(false, "У команды execute_script должен быть ровно один аргумент!");
            }

            console.println("Запуск скрипта '" + fileName + "'");

            try (Scanner input = new Scanner(new File(fileName), "UTF-8")) {
                Console fileConsole = new FileConsole(input);

                while (input.hasNextLine()) {
                    String line = input.nextLine().trim();
                    if (line.isEmpty()) continue;

                    if (line.equals("exit")) {
                        console.println("Скрипт завершён");
                        scriptStackCounter--;
                        return new ExecutionStatus(true, "Скрипт успешно выполнен");
                    }

                    Request request = prepareRequest(fileConsole, line);
                    if (request == null) {
                        scriptStackCounter--;
                        return new ExecutionStatus(false, "Выполнение скрипта остановлено из-за ошибки");
                    }

                    networkManager.send(request);
                    Response response = networkManager.receive();
                    ExecutionStatus commandStatus = response.getExecutionStatus();

                    if (commandStatus.isSuccess()) {
                        console.println(commandStatus.getMessage());
                    } else {
                        console.printError(commandStatus.getMessage());
                        scriptStackCounter--;
                        return new ExecutionStatus(false, "Выполнение скрипта приостановлено");
                    }
                }
            } catch (FileNotFoundException e) {
                scriptStackCounter--;
                return new ExecutionStatus(false, "Файл скрипта не найден: " + fileName);
            } catch (IOException e) {
                scriptStackCounter--;
                return new ExecutionStatus(false, "Ошибка при чтении файла скрипта: " + e.getMessage());
            }

            scriptStackCounter--;
            return new ExecutionStatus(true, "Скрипт успешно выполнен");

        } catch (Exception e) {
            scriptStackCounter--;
            return new ExecutionStatus(false, "Ошибка при выполнении скрипта: " + e.getMessage());
        }
    }

    
    private static ExecutionStatus validateCommand(String[] userCommand) {
        try {
            if (userCommand[0].equals("exit")) {
                return new ExecutionStatus(true, "");
            }

            if (commandsData == null) {
                return new ExecutionStatus(false, "Список команд ещё не получен от сервера");
            }

            if (userCommand[0].equals("execute_script")) {
                return new ExecutionStatus(true, "Введена команда 'execute_script'");
            }

            Pair<ArgumentValidator, Boolean> commandInfo = commandsData.get(userCommand[0]);
            if (commandInfo == null) {
                return new ExecutionStatus(false, "Команда '" + userCommand[0] + "' не найдена! Введите 'help' для списка команд.");
            }

            ArgumentValidator validator = commandInfo.getFirst();
            String args = userCommand.length > 1 ? userCommand[1] : "";
            return validator.validate(args, userCommand[0]);

        } catch (NullPointerException e) {
            return new ExecutionStatus(false, "Введено недостаточно аргументов для выполнения команды!");
        }
    }
}
