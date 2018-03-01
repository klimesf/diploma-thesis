package cz.filipklimes.diploma.framework.businessContext.loader.remote;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;

import java.util.*;

@FunctionalInterface
public interface RemoteLoader
{

    Set<BusinessContext> loadContexts(Set<BusinessContextIdentifier> identifiers);

}
