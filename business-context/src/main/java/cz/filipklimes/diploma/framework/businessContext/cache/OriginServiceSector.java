package cz.filipklimes.diploma.framework.businessContext.cache;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class OriginServiceSector
{

    @Getter
    private final String originServiceName;

    private boolean valid;

    private final Map<String, BusinessContextSector> rules;

    public OriginServiceSector(final String originServiceName)
    {
        this.originServiceName = originServiceName;
        this.valid = false;
        this.rules = new HashMap<>();
    }

    /**
     * Stores collection of business rules received from single origin service.
     *
     * @param newRules The rules to store.
     */
    public void store(final Collection<BusinessRule> newRules)
    {
        if (valid) {
            throw new IllegalStateException("Cannot store rules in valid cache");
        }

        Objects.requireNonNull(newRules);

        // Split rules by their applicable contexts
        Map<String, Set<BusinessRule>> rulesByContext = new HashMap<>();
        newRules.forEach(rule -> {
            rule.getApplicableContexts()
                .forEach(contextName ->
                    rulesByContext.computeIfAbsent(contextName, key -> new HashSet<>()).add(rule)
                );
        });

        // Store the rules to their appropriate sectors
        rulesByContext.forEach((contextName, contextRules) -> rules
            .computeIfAbsent(contextName, BusinessContextSector::new)
            .store(contextRules)
        );

        valid = true;
    }

    public void invalidate()
    {
        rules.clear();
        valid = false;
    }

    public boolean isValid()
    {
        return valid;
    }

    public Collection<BusinessRule> findPreconditions(final String businessContextName)
    {
        Objects.requireNonNull(businessContextName);

        return Optional.ofNullable(rules.get(businessContextName))
            .map(BusinessContextSector::getPreconditions)
            .orElse(Collections.emptySet());
    }

    public Collection<BusinessRule> findPostConditions(final String businessContextName)
    {
        Objects.requireNonNull(businessContextName);

        return Optional.ofNullable(rules.get(businessContextName))
            .map(BusinessContextSector::getPostConditions)
            .orElse(Collections.emptySet());
    }

    public Set<BusinessRule> getAllRules()
    {
        return rules.values().stream()
            .map(BusinessContextSector::getAllRules)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

}
