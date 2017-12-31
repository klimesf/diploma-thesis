package cz.filipklimes.diploma.framework.businessContext.provider;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;

import java.util.*;

@FunctionalInterface
public interface BusinessContextProvider
{

    /**
     * Returns business rules defined locally.
     *
     * @return Set od local business rules.
     */
    Set<BusinessRule> getLocalRules();

}
