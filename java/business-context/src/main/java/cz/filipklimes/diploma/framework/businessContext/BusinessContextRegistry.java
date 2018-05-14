package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedBusinessContextException;
import cz.filipklimes.diploma.framework.businessContext.loader.LocalBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import org.apache.commons.lang3.SerializationUtils;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class BusinessContextRegistry
{

    private static final Logger logger = Logger.getLogger(BusinessContextRegistry.class.getName());
    private boolean transactionInProgress = false;

    private final LocalBusinessContextLoader localLoader;
    private final RemoteBusinessContextLoader remoteLoader;
    private Map<BusinessContextIdentifier, BusinessContext> localContexts;
    private Map<BusinessContextIdentifier, BusinessContext> contexts;
    private final List<BusinessContext> transactionChanges = new ArrayList<>();

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
        return localContexts.entrySet().stream()
            .map(Map.Entry::getValue)
            .collect(Collectors.toSet());
    }

    /**
     * Saves business context.
     *
     * @param businessContext The new or updated business context.
     */
    public void saveOrUpdateBusinessContext(final BusinessContext businessContext)
    {
        if (!transactionInProgress) {
            throw new RuntimeException("There must be a transaction in progress");
        }
        transactionChanges.add(businessContext);
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

    public void beginTransaction()
    {
        logger.info("Starting transaction");
        transactionInProgress = true;
        transactionChanges.clear();
    }

    public void commitTransaction()
    {
        logger.info("Commiting transaction");
        for (BusinessContext businessContext : transactionChanges) {
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
        }

        transactionChanges.clear();
        transactionInProgress = false;
    }

    public void rollbackTransaction()
    {
        logger.info("Rolling back transaction");
        transactionChanges.clear();
        transactionInProgress = false;
    }

    public void waitForTransaction()
    {
        while (transactionInProgress) {
            try {
                Thread.sleep(100); // Spin lock
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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
