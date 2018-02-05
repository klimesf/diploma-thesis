package cz.filipklimes.diploma.framework.businessContext.exception;

import lombok.Getter;

public class UndefinedMethodException extends RuntimeException
{

    @Getter
    private String name;

    public UndefinedMethodException(final String name)
    {
        super(String.format("Undefined method: %s", name));
        this.name = name;
    }

}
