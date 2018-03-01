package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.loader.LocalBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteServiceAddress;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BusinessContextRegistry
{

    private final LocalBusinessContextLoader localLoader;
    private final RemoteBusinessContextLoader remoteLoader;
    private Map<BusinessContextIdentifier, BusinessContext> contexts;

    private BusinessContextRegistry(
        final LocalBusinessContextLoader localLoader,
        final RemoteBusinessContextLoader remoteLoader
    )
    {
        this.localLoader = Objects.requireNonNull(localLoader);
        this.remoteLoader = Objects.requireNonNull(remoteLoader);
        this.initialize();
    }

    /**
     * Initializes the registry by loading local business contexts,
     * including remote contexts if necessary and storing them to local cache.
     */
    public final void initialize()
    {
        // Load local contexts
        contexts = localLoader.load()
            .stream()
            .collect(Collectors.toMap(
                BusinessContext::getIdentifier,
                Function.identity(),
                (c1, c2) -> {
                    throw new IllegalStateException(String.format("Two local business contexts with same identifier: %s", c1.toString()));
                }
            ));

        // Analyze and find out which contexts to fetch
        Set<BusinessContextIdentifier> includedRemoteContexts = contexts.values()
            .stream()
            .map(BusinessContext::getIncludedContexts)
            .flatMap(Set::stream)
            .filter(contextName -> !contexts.containsKey(contextName))
            .collect(Collectors.toSet());

        // Load remote contexts
        Map<BusinessContextIdentifier, BusinessContext> remoteContexts = remoteLoader.loadContextsByIdentifier(includedRemoteContexts);

        // Include remote contexts into the local ones
        contexts.forEach((name, context) -> {
            context.getIncludedContexts().forEach(included -> {
                if (!remoteContexts.containsKey(included)) {
                    throw new RuntimeException(String.format("Could not fetch remote business context %s", included));
                }
                context.merge(remoteContexts.get(included));
            });
        });
    }

    public BusinessContext getContextByName(final BusinessContextIdentifier identifier)
    {
        if (!contexts.containsKey(identifier)) {
            throw new RuntimeException(String.format("No business context with identifier: %s", identifier));
        }
        return contexts.get(identifier);
    }

    public Map<BusinessContextIdentifier, BusinessContext> getContextByNames(final Set<BusinessContextIdentifier> identifier)
    {
        return Objects.requireNonNull(identifier).stream()
            .map(this::getContextByName)
            .collect(Collectors.toMap(
                BusinessContext::getIdentifier,
                Function.identity()
            ));
    }

    public void markRulesAsIncluded(final RemoteServiceAddress remoteServiceAddress, Set<BusinessContextIdentifier> identifiers)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private LocalBusinessContextLoader localLoader;
        private RemoteBusinessContextLoader remoteLoader;

        private Builder()
        {
        }

        public Builder withLocalLoader(final LocalBusinessContextLoader localLoader)
        {
            this.localLoader = localLoader;
            return this;
        }

        public Builder withRemoteLoader(final RemoteBusinessContextLoader remoteLoader)
        {
            this.remoteLoader = remoteLoader;
            return this;
        }

        public BusinessContextRegistry build()
        {
            return new BusinessContextRegistry(localLoader, remoteLoader);
        }

    }

}
