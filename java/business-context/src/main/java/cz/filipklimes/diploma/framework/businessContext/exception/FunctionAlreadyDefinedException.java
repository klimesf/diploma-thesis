package cz.filipklimes.diploma.framework.businessContext.exception;

import lombok.Getter;

public class FunctionAlreadyDefinedException extends RuntimeException
{

    @Getter
    private String name;

    public FunctionAlreadyDefinedException(final String name)
    {
        super(String.format("Function already defined: %s", name));
        this.name = name;
    }

}
