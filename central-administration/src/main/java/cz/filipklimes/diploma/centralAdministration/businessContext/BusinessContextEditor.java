package cz.filipklimes.diploma.centralAdministration.businessContext;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteLoader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BusinessContextEditor
{

    private Map<BusinessContextIdentifier, BusinessContext> contexts;
    private Collection<RemoteLoader> loaders;

    public BusinessContextEditor(final Collection<RemoteLoader> loaders)
    {
        this.loaders = Collections.unmodifiableCollection(loaders);
        refresh();
    }

    public void refresh()
    {
        contexts = loaders.stream()
            .map(RemoteLoader::loadAllContexts)
            .flatMap(Set::stream)
            .collect(Collectors.toMap(
                BusinessContext::getIdentifier,
                Function.identity(),
                (c1, c2) -> c1 // Ignore conflicts
            ));
    }

    public Map<BusinessContextIdentifier, BusinessContext> getContexts()
    {
        return Collections.unmodifiableMap(contexts);
    }

    public BusinessContext getContext(final BusinessContextIdentifier identifier)
    {
        return contexts.get(identifier);
    }
}
