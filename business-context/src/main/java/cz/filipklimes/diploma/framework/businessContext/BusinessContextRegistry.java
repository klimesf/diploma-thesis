package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.cache.OriginServiceSector;
import cz.filipklimes.diploma.framework.businessContext.loader.BusinessRulesLoader;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public final class BusinessContextRegistry
{

    private static final String LOCAL_ORIGIN = "local";

    private BusinessRulesLoader localLoader;
    private Map<String, BusinessRulesLoader> remoteLoaders;
    private Map<String, OriginServiceSector> rulesCache;
    private boolean initialized;

    private BusinessContextRegistry(
        final BusinessRulesLoader localLoader,
        final Map<String, BusinessRulesLoader> remoteLoaders
    )
    {
        this.localLoader = Objects.requireNonNull(localLoader);
        this.remoteLoaders = Collections.unmodifiableMap(Objects.requireNonNull(remoteLoaders));
        this.rulesCache = new HashMap<>();
        this.initialized = false;
    }

    /**
     * Initializes the registry by loading both local and remote business rules
     * and storing them to local cache.
     */
    public void initialize()
    {
        // Load & store local rules
        rulesCache.computeIfAbsent(LOCAL_ORIGIN, OriginServiceSector::new).store(localLoader.load());

        // Load & store remote rules
        remoteLoaders.entrySet().stream()
            .map(entry -> new RemoteRules(entry.getKey(), entry.getValue().load()))
            .forEach(entry -> rulesCache.computeIfAbsent(entry.getOriginName(), OriginServiceSector::new).store(entry.getRules()));

        initialized = true;
    }

    /**
     * Finds all preconditions applicable to the business context.
     *
     * @param businessContextName Name of the business context.
     * @return Applicable preconditions.
     */
    public Set<BusinessRule> findPreconditions(final String businessContextName)
    {
        if (!initialized) {
            throw new IllegalStateException("Registry must be initialized first");
        }
        return rulesCache.entrySet().stream()
            .peek(this::refreshInvalidSector)
            .map(sector -> sector.getValue().findPreconditions(businessContextName))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    /**
     * Finds all post-conditions applicable to the business context.
     *
     * @param businessContextName Name od the business context.
     * @return Applicable post-conditions.
     */
    public Set<BusinessRule> findPostConditions(final String businessContextName)
    {
        if (!initialized) {
            throw new IllegalStateException("Registry must be initialized first");
        }
        return rulesCache.entrySet().stream()
            .peek(this::refreshInvalidSector)
            .map(sector -> sector.getValue().findPostConditions(businessContextName))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    /**
     * Invalidates registry cache sector belonging to single remote business rule origin.
     *
     * @param originServiceName Name of the remote origin.
     */
    public void invalidateRemoteOrigin(final String originServiceName)
    {
        if (!initialized) {
            throw new IllegalStateException("Registry must be initialized first");
        }
        Optional.ofNullable(rulesCache.get(originServiceName))
            .ifPresent(OriginServiceSector::invalidate);
    }

    public Set<BusinessRule> getLocalRules()
    {
        if (!initialized) {
            throw new IllegalStateException("Registry must be initialized first");
        }
        return rulesCache.get(LOCAL_ORIGIN).getAllRules();
    }

    public Map<String, Set<BusinessRule>> getAllRules()
    {
        if (!initialized) {
            throw new IllegalStateException("Registry must be initialized first");
        }
        return rulesCache.entrySet().stream()
            .peek(this::refreshInvalidSector)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().getAllRules()
            ));
    }

    private void refreshInvalidSector(final Map.Entry<String, OriginServiceSector> sector)
    {
        if (!initialized) {
            throw new IllegalStateException("Registry must be initialized first");

        }

        if (!sector.getValue().isValid()) {
            Set<BusinessRule> newRules = remoteLoaders.get(sector.getKey()).load();
            sector.getValue().store(newRules);
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private BusinessRulesLoader localLoader;
        private final Map<String, BusinessRulesLoader> remoteLoaders;

        private Builder()
        {
            this.remoteLoaders = new HashMap<>();
        }

        public Builder setLocalLoader(final BusinessRulesLoader loader)
        {
            localLoader = Objects.requireNonNull(loader);
            return this;
        }

        public Builder addRemoteLoader(final String originName, final BusinessRulesLoader loader)
        {
            Objects.requireNonNull(originName);
            Objects.requireNonNull(loader);

            if (remoteLoaders.containsKey(originName)) {
                throw new IllegalArgumentException("Builder already contains loader for the specified remote origin");
            }

            remoteLoaders.put(originName, loader);
            return this;
        }

        public BusinessContextRegistry build()
        {
            return new BusinessContextRegistry(localLoader, remoteLoaders);
        }

    }

    private static final class RemoteRules
    {

        @Getter
        private final String originName;

        @Getter
        private final Set<BusinessRule> rules;

        private RemoteRules(final String originName, final Set<BusinessRule> rules)
        {
            this.originName = originName;
            this.rules = rules;
        }

    }

}
