package cz.filipklimes.diploma.framework.businessContext.exception;

import lombok.Getter;

public class MethodAlreadyDefinedException extends RuntimeException
{

    @Getter
    private String name;

    public MethodAlreadyDefinedException(final String name)
    {
        super(String.format("Method already defined: %s", name));
        this.name = name;
    }

}
