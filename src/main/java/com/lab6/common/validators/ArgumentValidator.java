package com.lab6.common.validators;

import com.lab6.common.Sup.ExecutionStatus;

import java.io.Serial;
import java.io.Serializable;

public abstract class ArgumentValidator {

    public abstract ExecutionStatus validate(String arg, String name);
}
