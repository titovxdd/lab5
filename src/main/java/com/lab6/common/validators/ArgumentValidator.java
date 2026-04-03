package com.lab6.common.validators;

import com.lab6.common.Sup.ExecutionStatus;

public abstract class ArgumentValidator {

    public abstract ExecutionStatus validate(String arg);
}
