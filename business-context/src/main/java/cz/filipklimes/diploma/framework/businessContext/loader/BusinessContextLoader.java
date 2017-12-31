package cz.filipklimes.diploma.framework.businessContext.loader;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;

import java.util.*;

@FunctionalInterface
public interface BusinessContextLoader
{

    /**
     * Loads business rules from various sources.
     *
     * @return Set of business rules.
     */
    Set<BusinessRule> load();

}
