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
    private static final String SERVER_HOST = "localhost";

    private static Map<String, Pair<ArgumentValidator, Boolean>> commandsData;
    private static final ClientNetworkManager networkManager = new ClientNetworkManager(SERVER_PORT, SERVER_HOST);
    private static int connectionAttempts = 1;
    private static int scriptStackCounter = 0;

    public static void main(String[] args) {
        do {
            try {
                networkManager.connect();
                console.println("Успешно подключено к " + SERVER_HOST + ":" + SERVER_PORT);
                console.println("Для получения списка команд введите 'help'");
                connectionAttempts = 1;
                console.println("Connected to " + SERVER_HOST + ":" + SERVER_PORT);
                while (true) {
                    String inputCommand = console.readln();
                    ExecutionStatus argumentStatus = validateCommand((inputCommand.trim() + " ").split(" ", 2));
                    if (!argumentStatus.isSuccess()) {
                        console.printError(argumentStatus.getMessage());
                    }
                    else {
                        Request request = prepareRequest(console, inputCommand);
                        if (request == null) {
                            continue;
                        }

                        networkManager.send(request);
                        Response response = networkManager.receive();
                        if (response.getExecutionStatus().isSuccess()) {
                            if (response.getExecutionStatus().getMessage() == null) {
                                response.getExecutionStatus().getCollection().forEach(item -> console.println(item.toString()));
                            }
                            else {
                                console.println(response.getExecutionStatus().getMessage());
                            }
                        } else {
                            console.printError(response.getExecutionStatus().getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                console.printError("Не удалось подключиться к серверу. Проверьте, запущен ли сервер и доступен ли он по адресу " + SERVER_HOST + ":" + SERVER_PORT);
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

    private static Request askingRequest(Console console, String inputCommand) {
        ElementValidator elementValidator = new ElementValidator();
        Pair<ExecutionStatus, MusicBand> validationStatusPair = elementValidator.validateAsking(console, 1L);
        if (!validationStatusPair.getFirst().isSuccess()) {
            console.printError(validationStatusPair.getFirst().getMessage());
            return null;
        } else {
            return new Request(inputCommand, validationStatusPair.getSecond());
        }
    }

    private static Request prepareRequest(Console console, String inputCommand) {
        String[] commands = (inputCommand.trim() + " ").split(" ", 2);
        if (commandsData.get(commands[0]).getSecond()) {
            return askingRequest(console, inputCommand); // Если команда требует построчного ввода
        } else if (commands[0].equals("execute_script")) {
            ExecutionStatus scriptStatus = runScript(commands[1].trim());
            if (!scriptStatus.isSuccess()) {
                console.printError(scriptStatus.getMessage());
                return null;
            }
            return null;
        } else {
            return new Request(inputCommand);
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
                return new ExecutionStatus(false, "У команды execute_script должен быть ровно один аргумент!\nПример корректного ввода: execute_script file_name");
            }
            console.println("Запуск скрипта '" + fileName + "'");
            try (Scanner input = new Scanner(new File(fileName), "UTF-8")) {
                Console FileConsole = new FileConsole(input);
                while (input.hasNextLine()) {
                    String line = input.nextLine().trim();
                    if (!line.equals("exit")) {

                        Request request = prepareRequest(FileConsole, line);
                        if (request == null) {
                            return new ExecutionStatus(false, "Выполнение скрипта остановлено");
                        }
                        networkManager.send(request);
                        Response response = networkManager.receive();
                        ExecutionStatus commandStatus = response.getExecutionStatus();

                        if (response.getExecutionStatus().isSuccess()) {
                            console.println(commandStatus.getMessage());
                        } else {
                            if (!commandStatus.getMessage().equals("Выполнение скрипта приостановлено.")) {
                                console.printError(commandStatus.getMessage());
                            }
                            return new ExecutionStatus(false, "Выполнение скрипта приостановлено.");
                        }
                    } else {
                        scriptStackCounter--;
                        return new ExecutionStatus(true, "Скрипт успешно выполнен.");
                    }
                }
            } catch (FileNotFoundException e) {
                return new ExecutionStatus(false, "Не удаётся найти файл скрипта!");
            } catch (IllegalArgumentException e) {
                return new ExecutionStatus(false, "Произошла ошибка при чтении данных из файла скрипта!");
            } catch (Exception e) {
                return new ExecutionStatus(false, "Произошла ошибка при выполнении команды скрипта!");
            }
            return new ExecutionStatus(true, "");
        } catch (Exception e) {
            return new ExecutionStatus(false, "Произошла ошибка при запуске скрипта!");
        }
    }

    private static ExecutionStatus validateCommand(String[] userCommand) {
        try {
            if (userCommand[0].equals("exit")) {
                console.println("Завершение работы клиента");
                try {
                    networkManager.close();
                } catch (IOException e) {
                    console.printError("Не удалось закрыть соединение с сервером.");
                }
                System.exit(0);
                return null;
            } else if (userCommand[0].equals("execute_script")) {
                return new ExecutionStatus(true, "Введена команда 'execute_script'. Валидация аргументов не требуется.");
            } else {
                var argumentValidator = commandsData.get(userCommand[0]);
                if (argumentValidator == null) {
                    return new ExecutionStatus(false, "Команда '" + userCommand[0] + "' не найдена! Для показа списка команд введите 'help'.");
                } else {
                    return argumentValidator.getFirst().validate(userCommand[1].trim());
                }
            }
        } catch (NullPointerException e) {
            return new ExecutionStatus(false, "Введено недостаточно аргументов для выполнения последней команды!");
        }
    }
}
