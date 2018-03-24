package cz.filipklimes.diploma.framework.businessContext.exception;

import cz.filipklimes.diploma.framework.businessContext.Precondition;
import lombok.Getter;

import java.util.*;

public class BusinessRulesCheckFailedException extends RuntimeException
{

    @Getter
    private final Set<Precondition> failedRules;

    public BusinessRulesCheckFailedException(final Set<Precondition> failedRules)
    {
        super(String.format("BusinessRules check failed, %d rules were not satisfied", failedRules.size()));
        this.failedRules = Collections.unmodifiableSet(failedRules);
    }

}
