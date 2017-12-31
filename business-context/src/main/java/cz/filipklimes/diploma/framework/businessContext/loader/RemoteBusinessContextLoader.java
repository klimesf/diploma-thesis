package cz.filipklimes.diploma.framework.businessContext.loader;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;

import java.util.*;

public class RemoteBusinessContextLoader implements BusinessContextLoader
{

    private final String remoteApiAddress;

    public RemoteBusinessContextLoader(final String remoteApiAddress)
    {
        this.remoteApiAddress = remoteApiAddress;
    }

    @Override
    public Set<BusinessRule> load()
    {
        return null;
    }

}
