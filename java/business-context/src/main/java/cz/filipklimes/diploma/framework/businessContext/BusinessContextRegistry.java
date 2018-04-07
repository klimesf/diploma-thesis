package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedBusinessContextException;
import cz.filipklimes.diploma.framework.businessContext.loader.LocalBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import org.apache.commons.lang3.SerializationUtils;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BusinessContextRegistry
{

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private final LocalBusinessContextLoader localLoader;
    private final RemoteBusinessContextLoader remoteLoader;
    private Map<BusinessContextIdentifier, BusinessContext> localContexts;
    private Map<BusinessContextIdentifier, BusinessContext> contexts;

    private BusinessContextRegistry(
        final LocalBusinessContextLoader localLoader,
        final RemoteBusinessContextLoader remoteLoader
    )
    {
        this.localLoader = Objects.requireNonNull(localLoader);
        this.remoteLoader = Objects.requireNonNull(remoteLoader);
        this.contexts = new HashMap<>();
        this.initialize();
    }

    /**
     * Initializes the registry by loading local business contexts,
     * including remote contexts if necessary and storing them to local cache.
     */
    public final void initialize()
    {
        writeLock.lock();
        try {
            // Load local contexts
            localContexts = localLoader.load()
                .stream()
                .collect(Collectors.toMap(
                    BusinessContext::getIdentifier,
                    Function.identity(),
                    (c1, c2) -> {
                        throw new IllegalStateException(String.format("Two local business contexts with same identifier: %s", c1.toString()));
                    }
                ));

            localContexts.forEach((key, value) -> {
                contexts.put(key, SerializationUtils.clone(value));
            });

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
                mergeRemoteContexts(context, remoteContexts);
            });

        } finally {
            writeLock.unlock();
        }
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
        readLock.lock();
        try {
            if (!contexts.containsKey(identifier)) {
                throw new UndefinedBusinessContextException(identifier);
            }
            return contexts.get(identifier);

        } finally {
            readLock.unlock();
        }
    }

    public Map<BusinessContextIdentifier, BusinessContext> getContextsByIdentifiers(final Set<BusinessContextIdentifier> identifiers)
    {
        readLock.lock();
        try {
            return Objects.requireNonNull(identifiers).stream()
                .map(this::getContextByIdentifier)
                .collect(Collectors.toMap(
                    BusinessContext::getIdentifier,
                    Function.identity()
                ));

        } finally {
            readLock.unlock();
        }
    }

    public Set<BusinessContext> getAllContexts()
    {
        readLock.lock();
        try {
            return localContexts.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());

        } finally {
            readLock.unlock();
        }
    }

    /**
     * Saves business context.
     *
     * @param businessContext The new or updated business context.
     */
    public void saveOrUpdateBusinessContext(final BusinessContext businessContext)
    {
        writeLock.lock();
        try {
            BusinessContextIdentifier identifier = businessContext.getIdentifier();
            localContexts.put(identifier, SerializationUtils.clone(businessContext));

            // Analyze and find out which contexts to fetch
            Set<BusinessContextIdentifier> includedRemoteContexts = businessContext
                .getIncludedContexts().stream()
                .filter(contextName -> !contexts.containsKey(contextName))
                .collect(Collectors.toSet());

            // Load remote contexts
            Map<BusinessContextIdentifier, BusinessContext> remoteContexts = remoteLoader.loadContextsByIdentifier(includedRemoteContexts);

            // Include remote contexts into the local ones
            mergeRemoteContexts(businessContext, remoteContexts);
            contexts.put(identifier, businessContext);

        } finally {
            writeLock.unlock();
        }
    }

    private void mergeRemoteContexts(final BusinessContext businessContext, final Map<BusinessContextIdentifier, BusinessContext> remoteContexts)
    {
        businessContext.getIncludedContexts().forEach(included -> {
            if (contexts.containsKey(included)) {
                businessContext.merge(contexts.get(included));
                return;
            }
            if (!remoteContexts.containsKey(included)) {
                throw new RuntimeException(String.format("Could not fetch remote business context %s", included));
            }
            businessContext.merge(remoteContexts.get(included));
        });
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
