package cz.filipklimes.diploma.framework.businessContext.provider;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.loader.LocalDroolsBusinessContextLoader;

import java.util.*;

public class DroolsBusinessContextProvider implements BusinessContextProvider
{

    private final LocalDroolsBusinessContextLoader loader;

    public DroolsBusinessContextProvider(final LocalDroolsBusinessContextLoader loader)
    {
        this.loader = loader;
    }

    @Override
    public Set<BusinessRule> getLocalRules()
    {
        return loader.load();
    }

}
