package cz.filipklimes.diploma.framework.businessContext.exception;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import lombok.Getter;

import java.util.*;

public class BusinessRulesCheckFailedException extends Exception
{

    @Getter
    private Set<BusinessRule> failedRules;

    public BusinessRulesCheckFailedException(final Set<BusinessRule> failedRules)
    {
        super(String.format("BusinessRules check failed, %d rules were not satisfied", failedRules.size()));
        this.failedRules = Collections.unmodifiableSet(failedRules);
    }

}
