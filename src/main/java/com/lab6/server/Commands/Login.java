package com.lab6.server.Commands;

import com.lab6.common.Sup.ExecutionStatus;
import com.lab6.common.Sup.Pair;
import com.lab6.common.validators.EmptyValidator;
import com.lab6.server.managers.DBManager;

public class Login extends Command{

    public Login(){
        super("login", "", new EmptyValidator());
    }
    @Override
    public ExecutionStatus execute(String arg, Pair<String, String> user) {
        return DBManager.getInstance().checkPassword(user);
    }
}
