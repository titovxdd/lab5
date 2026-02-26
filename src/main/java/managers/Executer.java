package managers;

import sup.Console;
import sup.Status;

public class Executer {
    private Console console;
    public int scriptStackCounter = 0;

    public Executer(Console console){
        this.console = console;
    }

    public Status runCommand(String[] args){
        try {
            var command = Commands.getCommand(args[0]);
            if (command == null){
                return new Status(false, "Команда '" + args[0] + "' не найдена");
            } else {
                if (command.parse(args[1]).isSuccess()) {
                    console.println("Выполнение команды '" + args[0] + "'");
                    return command.execute(args[1]);
                } else {
                    return command.parse(args[1]);
                }
            }
        }  catch (NullPointerException e) {
            return new Status(false, "Введено недостаточно аргументов для выполнения последней команды");
        } catch (Exception e) {
            return new Status(false, "Ошибка при выполнении команды");
        }
    }

    public void interactiveMode() {
        try {
            while (true){
                String[] args = (console.readln().trim() + " ").split(" ", 2);
                args[1] = args[1].trim();
                Status commandStatus = runCommand(args);
                if (commandStatus.isSuccess()) {
                    console.println(commandStatus.getMessage());
                } else {
                    console.printError(commandStatus.getMessage());
                }
            }
        } catch (Exception e) {
            console.printError("Произошла ошибка выполнения команды");
        }
    }
}
