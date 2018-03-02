package cz.filipklimes.diploma.framework.businessContext.exception;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import lombok.Getter;

public class UndefinedBusinessContextException extends RuntimeException
{

    @Getter
    private final BusinessContextIdentifier identifier;

    public UndefinedBusinessContextException(final BusinessContextIdentifier identifier)
    {
        super(String.format("Undefiend business context with identifier: %s", identifier));
        this.identifier = identifier;
    }

}
