package Commands;

import managers.Commands;
import managers.Executer;
import sup.Console;
import sup.FileConsole;
import sup.Status;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ExecuteScript extends Command{

    private Commands commands;
    private final Executer executer;

    public ExecuteScript(Console console, Executer executer){
        super("execute_script", "считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме", console);
        this.executer = executer;
    }

    @Override
    public Status execute(String fileName){
        try {
            executer.scriptStackCounter++;
            if (executer.scriptStackCounter > 5) {
                executer.scriptStackCounter--;
                return new Status(false, "Превышена максимальная глубина рекурсии");
            }
            if (fileName.isEmpty()) {
                executer.scriptStackCounter--;
                return new Status(false, "У команды execute_script должен быть только один аргумент!\nПример корректного ввода: execute_script file_name");
            }
            console.println("Запуск скрипта '" + fileName + "'");
            try (Scanner scanner = new Scanner(new File(fileName))) {
                Console FileConsole = new FileConsole(scanner);
                Commands.getCommand("add").updateConsole(FileConsole);
                Commands.getCommand("add_if_max").updateConsole(FileConsole);
                Commands.getCommand("add_if_min").updateConsole(FileConsole);
                Commands.getCommand("update").updateConsole(FileConsole);
                while (executer.scriptStackCounter > 0) {
                    String line = scanner.nextLine();
                    if (!line.equals("exit")) {
                        String[] inputCommand = (line.trim() + " ").split(" ", 2);
                        inputCommand[1] = inputCommand[1].trim();
                        Status commandStatus = executer.runCommand(inputCommand);

                        if (commandStatus.isSuccess()) {
                            console.println(commandStatus.getMessage());
                        } else {
                            if (!commandStatus.getMessage().equals("Выполнение скрипта приостановлено.")) {
                                console.printError(commandStatus.getMessage());
                            }
                            return new Status(false, " ");
                        }
                    } else {
                        executer.scriptStackCounter--;
                        return new Status(true, "Скрипт успешно выполнен.");
                    }
                }
            } catch (FileNotFoundException e) {
                return new Status(false, "Не удаётся найти файл скрипта");
            } catch (IllegalArgumentException e) {
                return new Status(false, "Произошла ошибка при чтении данных из файла скрипта");
            } catch (Exception e) {
                return new Status(false, "Произошла ошибка при выполнении команды скрипта");
            }
            return new Status(true, "");
        } catch (Exception e) {
            return new Status(false, "Произошла ошибка при запуске скрипта!");
        }
        finally {
            Commands.getCommand("add").updateConsole(console);
            Commands.getCommand("add_if_max").updateConsole(console);
            Commands.getCommand("add_if_min").updateConsole(console);
            Commands.getCommand("update").updateConsole(console);
        }
    }

    @Override
    public Status parse(String arg){
        if (!(arg.isEmpty())) {
            try (Scanner scanner = new Scanner(new File(arg))) {
                return new Status(true, "Файл найден");
            } catch (FileNotFoundException e) {
                return new Status(false, "Файл не найден: " + arg);
            }
        } else {
            return new Status(false, "Команда должна иметь аргумент(file_name)");
        }
    }
}
