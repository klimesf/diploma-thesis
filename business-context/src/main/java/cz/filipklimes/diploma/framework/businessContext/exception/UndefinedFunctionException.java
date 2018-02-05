package cz.filipklimes.diploma.framework.businessContext.exception;

import lombok.Getter;

public class UndefinedFunctionException extends RuntimeException
{

    @Getter
    private final String name;

    public UndefinedFunctionException(final String name)
    {
        super(String.format("Undefined method: %s", name));
        this.name = name;
    }

}
