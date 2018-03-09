package cz.filipklimes.diploma.framework.businessContext.exception;

import lombok.Getter;

public class UndefinedVariableException extends RuntimeException
{

    @Getter
    private String name;

    public UndefinedVariableException(final String name)
    {
        super(String.format("Undefined variable: %s", name));
        this.name = name;
    }

}
