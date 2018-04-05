package cz.filipklimes.diploma.centralAdministration.businessContext.exception;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class MissingIncludedBusinessContextsException extends Exception
{

    @Getter
    private final Collection<BusinessContextIdentifier> identifiers;

    public MissingIncludedBusinessContextsException(final Collection<BusinessContextIdentifier> identifiers)
    {
        super(String.format(
            "Could not find included context with identifiers: %s",
            identifiers.stream()
                .map(BusinessContextIdentifier::toString)
                .collect(Collectors.joining(", "))
        ));
        this.identifiers = Collections.unmodifiableCollection(identifiers);
    }

}
