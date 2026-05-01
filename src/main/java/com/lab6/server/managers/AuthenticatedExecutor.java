package com.lab6.server.managers;

import com.lab6.common.Sup.ExecutionStatus;
import com.lab6.common.Sup.Pair;
import com.lab6.common.models.MusicBand;
import com.lab6.server.Server;

public class AuthenticatedExecutor {
    private final Executor executor;

    public AuthenticatedExecutor(Executor executor) {
        this.executor = executor;
    }

    public ExecutionStatus runCommand(String[] command, MusicBand band, Pair<String, String> user) {
        ExecutionStatus authStatus;
        if (command[0].equals("register") || command[0].equals("login")) {
            if ("register".equals(command[0])) {
                authStatus = DBManager.getInstance().addUser(user);  // Регистрация
            } else {
                authStatus = DBManager.getInstance().checkPassword(user);  // Вход
            }
            if (authStatus.isSuccess()) {
                Server.logger.info(authStatus.getMessage() + " User: " + user.getFirst());
            }
        } else {
            authStatus = DBManager.getInstance().checkPassword(user);
            if (authStatus.isSuccess()) {
                ExecutionStatus commandStatus = executor.runCommand(command, band, user);
                if (commandStatus.isSuccess()) {
                    Server.logger.info("Command '" + command[0] + "' executed successfully for user: " + user.getFirst());
                }
                return commandStatus;
            }
        }
        return authStatus;
    }
}
