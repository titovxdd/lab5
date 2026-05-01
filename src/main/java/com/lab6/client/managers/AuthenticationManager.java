package com.lab6.client.managers;

import com.lab6.client.sup.Console;
import com.lab6.common.Sup.Pair;
import com.lab6.common.Sup.Request;
import com.lab6.common.Sup.Response;

import java.io.IOException;

public class AuthenticationManager {
    public static Pair<String, String> sendAuthenticationRequest(
            ClientNetworkManager networkManager,
            Console console,
            Pair<String, String> user,
            String inputCommand) throws IOException, ClassNotFoundException {

        Request request = new Request(inputCommand, user);
        networkManager.send(request);
        Response authResponse = networkManager.receive();

        if (authResponse.getExecutionStatus().isSuccess()) {
            console.println(authResponse.getExecutionStatus().getMessage());
            return user;
        } else {
            console.printError(authResponse.getExecutionStatus().getMessage());
            return null;
        }
    }

    public static Pair<String, String> authenticateUser(
            ClientNetworkManager networkManager,
            Console console) throws IOException, ClassNotFoundException {

        while (true) {
            console.println("Введите команду 'register' для регистрации или 'login' для авторизации:");
            String inputCommand = console.readln().trim().toLowerCase();

            if (inputCommand.equals("register") || inputCommand.equals("login")) {
                console.println("Введите логин:");
                String username = console.readln();
                console.println("Введите пароль:");
                String password = console.readln();

                Pair<String, String> user = sendAuthenticationRequest(
                        networkManager, console,
                        new Pair<>(username, password),
                        inputCommand
                );

                if (user != null) {
                    console.println("Аутентификация успешна!");
                    return user;
                }
            } else {
                console.printError("Команда '" + inputCommand + "' не найдена! Используйте 'register' или 'login'.");
            }
        }
    }
}
