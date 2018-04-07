package cz.filipklimes.diploma.centralAdministration.businessContext;

import cz.filipklimes.diploma.centralAdministration.businessContext.exception.CyclicDependencyException;
import cz.filipklimes.diploma.centralAdministration.businessContext.exception.MissingIncludedBusinessContextsException;
import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BusinessContextEditor
{

    private static Logger log = LoggerFactory.getLogger(BusinessContextEditor.class);

    private Map<BusinessContextIdentifier, BusinessContext> contexts;
    private Map<String, RemoteLoader> loaders;
    private Map<BusinessContextIdentifier, Set<BusinessContextIdentifier>> includedBy;

    public BusinessContextEditor(final Map<String, RemoteLoader> loaders)
    {
        this.loaders = Collections.unmodifiableMap(loaders);
        this.includedBy = new HashMap<>();
        refresh();
    }

    public Map<BusinessContextIdentifier, BusinessContext> getContexts()
    {
        return Collections.unmodifiableMap(contexts);
    }

    public BusinessContext getContext(final BusinessContextIdentifier identifier)
    {
        return contexts.get(identifier);
    }

    public void updateContext(final BusinessContext context)
        throws MissingIncludedBusinessContextsException, CyclicDependencyException
    {
        BusinessContextAnalyzer.checkMissingContexts(contexts);
        BusinessContextAnalyzer.checkCyclicDependency(contexts);

        contexts.put(context.getIdentifier(), context);
        requestRemoteUpdate(context);

        refresh();
    }

    private void requestRemoteUpdate(final BusinessContext businessContext)
    {
        log.info(String.format("Requesting business context update for %s", businessContext.getIdentifier().toString()));

        RemoteLoader remoteLoader = loaders.get(businessContext.getIdentifier().getPrefix());
        if (remoteLoader == null) {
            throw new RuntimeException(String.format("No remote loader for prefix %s", businessContext.getIdentifier().getPrefix()));
        }

        remoteLoader.updateContext(businessContext);

        includedBy.getOrDefault(businessContext.getIdentifier(), Collections.emptySet())
            .forEach(affected -> requestRemoteUpdate(contexts.get(affected)));
    }

    private void refresh()
    {
        contexts = loaders.values().stream()
            .map(RemoteLoader::loadAllContexts)
            .flatMap(Set::stream)
            .collect(Collectors.toMap(
                BusinessContext::getIdentifier,
                Function.identity(),
                (c1, c2) -> c1 // Ignore conflicts
            ));

        contexts.forEach((key, value) -> value.getIncludedContexts().forEach(included -> {
            includedBy.computeIfAbsent(included, x -> new HashSet<>()).add(key);
        }));
    }

}
