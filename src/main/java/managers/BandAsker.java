package managers;

import models.*;
import sup.Console;
import sup.FileConsole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class BandAsker {

    public static class Breaker extends Exception { }

    public static class IllegalInputException extends IllegalArgumentException {
        public IllegalInputException(String message) {
            super(message);
        }
    }

    private static Person askPerson(Console console) throws Breaker {
        console.println("Ввод значений поля Person");
        String name;
        do {
            console.println("Введите значение поля name:");
            name = console.readln();
            if (name.equals("exit")) {
                throw new Breaker();
            }
        } while (name.isEmpty());
        LocalDate birthday = null;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            console.println("Введите значение поля birthday:");
            String br = console.readln();
            if (br.equals("exit")){
                throw new Breaker();
            } else {
                try {
                    birthday = LocalDate.parse(br);
                } catch (DateTimeParseException e) {
                    if (console instanceof FileConsole){
                        throw new IllegalInputException("Неверный формат даты");
                    }
                    console.printError("Неверный формат даты: " + br + "\nВерный формат: yyyy-mm-dd (например, 2022-12-22)");
                } catch (IllegalArgumentException e) {
                    console.printError(e.getMessage());
                }

            }
        } while (birthday == null);
        Color eyeColor = null;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            console.println("Введите значение поля eyeColor:");
            console.println("Список возможных значений: " + Color.list());
            String input = console.readln();
            if (input.equals("exit")) {
                throw new Breaker();
            } else {
                try {
                    eyeColor = Color.valueOf(input);
                } catch (IllegalArgumentException e) {
                    if (console instanceof FileConsole){
                        throw new IllegalArgumentException("Некорректное значение поля color");
                    }
                    console.printError("Некорректное значение поля color");
                }
            }
        } while (eyeColor == null);
        console.println("Данные поля Person успешно введены");
        return new Person(name, birthday, eyeColor);
    }

    private static Coordinates askCoordinates(Console console) throws Breaker, IllegalInputException {
        console.println("Ввод значений поля Coordinates:");
        Long x;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            console.println("Введите значение поля x:");
            try {
                String input = console.readln();
                if (input.equals("exit")) {
                    throw new Breaker();
                } else {
                    x = Long.parseLong(input);
                }
            } catch (NumberFormatException e) {
                x = 433L;
            }
            if (x > 432L) {
                if (console instanceof FileConsole){
                    throw new IllegalInputException("Некорректное значение поля x\nЗначение поля должно быть меньше 433");
                }
                console.printError("Некорректное значение поля x\nЗначение поля должно быть меньше 433");
            }
        } while (x > 432);

        Float y = null;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            console.println("Введите значение поля y:");
            try {
                String input = console.readln();
                if (input.equals("exit")) {
                    throw new Breaker();
                } else {
                    y = Float.parseFloat(input);
                }
            } catch (NumberFormatException e) {
                y = null;
            }
        } while (y == null);

        console.println("Значения поля Coordinates записаны");
        return new Coordinates(x, y);
    }

    public static MusicBand askBand(Console console, Long id) throws Breaker, IllegalInputException {
        String name;
        do {
            console.println("Введите значение поля name:");
            name = console.readln();
            if (name.equals("exit")) {
                throw new Breaker();
            }
        } while (name.isEmpty());

        Coordinates coordinates = askCoordinates(console);

        Long numberOfParticipants = -1L;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            console.println("Введите значение поля numberOfParticipants:");
            try {
                String input = console.readln();
                if (input.equals("exit")) {
                    throw new Breaker();
                } else {
                    numberOfParticipants = Long.valueOf(input);
                }
            } catch (NumberFormatException e) {
                numberOfParticipants = -1L;
            }
            if (numberOfParticipants <= 0) {
                if (console instanceof FileConsole){
                    throw new IllegalInputException("Некорректное значение поля numberOfParticipants\nЗначение поля должно быть больше 0");
                }
                console.printError("Некорректное значение поля numberOfParticipants\nЗначение поля должно быть больше 0");
            }
        } while (numberOfParticipants <= 0);

        Long singlesCount = -1L;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            console.println("Введите значение поля singlesCount:");
            try {
                String input = console.readln();
                if (input.equals("exit")) {
                    throw new Breaker();
                } else {
                    singlesCount = Long.valueOf(input);
                }
            } catch (NumberFormatException e) {
                singlesCount = -1L;
            }
            if (singlesCount <= 0) {
                if (console instanceof FileConsole){
                    throw new IllegalInputException("Некорректное значение поля singlesCount\nЗначение поля должно быть больше 0");
                }
                console.printError("Некорректное значение поля singlesCount\nЗначение поля должно быть больше 0");
            }
        } while (singlesCount <= 0);

        String description;
        do {
            console.println("Введите значение поля description:");
            description = console.readln();
            if (description.equals("exit")) {
                throw new Breaker();
            }
        } while (description.isEmpty());

        MusicGenre genre = null;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            console.println("Введите значение поля genre:");
            console.println("Список возможных значений: " + MusicGenre.list());
            String input = console.readln();
            if (input.equals("exit")) {
                throw new Breaker();
            } else {
                try {
                    genre = MusicGenre.valueOf(input);
                } catch (IllegalArgumentException e) {
                    if (console instanceof FileConsole){
                        throw new IllegalArgumentException("Некорректное значение поля genre");
                    }
                    console.printError("Некорректное значение поля genre");
                }
            }
        } while (genre == null);

        Person person = askPerson(console);

        MusicBand band = new MusicBand(id, name, LocalDate.now(), numberOfParticipants, description, coordinates, singlesCount, genre, person);
        if (band == null || !(band.validate())){
            System.out.println(band.toString());
        }
        return band;
    }

}
