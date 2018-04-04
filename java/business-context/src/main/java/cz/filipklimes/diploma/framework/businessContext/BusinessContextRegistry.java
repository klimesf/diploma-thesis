package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedBusinessContextException;
import cz.filipklimes.diploma.framework.businessContext.loader.LocalBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteServiceAddress;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BusinessContextRegistry
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
                if (contexts.containsKey(included)) {
                    context.merge(contexts.get(included));
                    return;
                }
                if (!remoteContexts.containsKey(included)) {
                    throw new RuntimeException(String.format("Could not fetch remote business context %s", included));
                }
                context.merge(remoteContexts.get(included));
            });
        });
    }

    /**
     * Retrieves business context with given identifier.
     *
     * @param identifier Identifier of the business context.
     * @return The business context with given identifier.
     * @throws UndefinedBusinessContextException when context with such identifier is not defined within the registry.
     */
    public BusinessContext getContextByIdentifier(final BusinessContextIdentifier identifier)
    {
        if (!contexts.containsKey(identifier)) {
            throw new UndefinedBusinessContextException(identifier);
        }
        return contexts.get(identifier);
    }

    public Map<BusinessContextIdentifier, BusinessContext> getContextsByIdentifiers(final Set<BusinessContextIdentifier> identifiers)
    {
        return Objects.requireNonNull(identifiers).stream()
            .map(this::getContextByIdentifier)
            .collect(Collectors.toMap(
                BusinessContext::getIdentifier,
                Function.identity()
            ));
    }

    public Set<BusinessContext> getAllContexts()
    {
        return contexts.entrySet().stream()
            .map(Map.Entry::getValue)
            .collect(Collectors.toSet());
    }

    /**
     * Saves business context.
     * @param businessContext The new or updated business context.
     * @return Set of remote services which should be notified about the change.
     */
    public synchronized Set<RemoteServiceAddress> saveOrUpdateBusinessContext(final BusinessContext businessContext)
    {
        // Import included contexts
        // Save to cache

        // TODO: implement
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
