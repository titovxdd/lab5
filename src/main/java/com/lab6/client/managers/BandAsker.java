package com.lab6.client.managers;

import com.lab6.client.Client;
import com.lab6.common.models.*;
import com.lab6.client.sup.Console;
import com.lab6.client.sup.FileConsole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

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
                console.printError("Некорректный формат\nОжидаемый формат: 5.5 или 5");
            }
        } while (y == null);

        console.println("Значения поля Coordinates записаны");
        return new Coordinates(x, y);
    }

    public static MusicBand askBand(Console console) throws Breaker, IllegalInputException {
        MusicBandBuilder builder = new MusicBandBuilder();
        String name;
        do {
            console.println("Введите значение поля name:");
            name = console.readln();
            if (name.equals("exit")) {
                throw new Breaker();
            }
        } while (name.isEmpty());
        builder.setName(name);

        builder.setCoordinates(askCoordinates(console));

        Long numberOfParticipants;
        do {
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
                if (console instanceof FileConsole) {
                    throw new IllegalInputException("Некорректное значение поля numberOfParticipants!\nЗначение поля должно быть больше 0");
                }
                console.printError("Некорректное значение поля numberOfParticipants!\nЗначение поля должно быть больше 0");
            }
        } while (numberOfParticipants <= 0);
        builder.setNumberOfParticipants(numberOfParticipants);

        Long albumsCount;
        do {
            console.println("Введите значение поля singlesCount:");
            try {
                String input = console.readln();
                if (input.equals("exit")) {
                    throw new Breaker();
                } else {
                    albumsCount = Long.valueOf(input);
                }
            } catch (NumberFormatException e) {
                albumsCount = -1L;
            }
            if (albumsCount <= 0) {
                if (console instanceof FileConsole) {
                    throw new IllegalInputException("Некорректное значение поля singlesCount!\nЗначение поля должно быть больше 0");
                }
                console.printError("Некорректное значение поля singlesCount!\nЗначение поля должно быть больше 0");
            }
        } while (albumsCount <= 0);
        builder.setSinglesCount(albumsCount);

        String description;
        do {
            console.println("Введите значение поля description:");
            description = console.readln();
            if (description.equals("exit")) {
                throw new Breaker();
            }
        } while (description.isEmpty());
        builder.setDescription(description);

        MusicGenre genre = null;
        do {
            console.println("Введите значение поля genre:");
            console.println("Список возможных значений: " + MusicGenre.list());
            String input = console.readln();
            if (input.equals("exit")) {
                throw new Breaker();
            } else {
                try {
                    genre = MusicGenre.valueOf(input);
                } catch (IllegalArgumentException e) {
                    if (console instanceof FileConsole) {
                        throw new IllegalArgumentException("Некорректное значение поля genre!");
                    }
                    console.printError("Некорректное значение поля genre!");
                }
            }
        } while (genre == null);
        builder.setGenre(genre);
        builder.setFrontMan(askPerson(console));

        return builder.setUser(Client.getCurrentUser().getFirst()).setCreationDate(LocalDate.now()).build();
    }

}
