package cz.filipklimes.diploma.framework.businessContext.cache;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleType;
import lombok.Getter;

import java.util.*;

public class BusinessContextSector
{

    @Getter
    private final String businessContextName;

    /**
     * BusinessRules with PRECONDITION type mapped by their name.
     */
    private final Map<String, BusinessRule> preconditions;

    /**
     * BusinessRules with POST_CONDITION type mapped by their name.
     */
    private final Map<String, BusinessRule> postConditions;

    public BusinessContextSector(final String businessContextName)
    {
        this.businessContextName = businessContextName;
        this.preconditions = new HashMap<>();
        this.postConditions = new HashMap<>();
    }

    public void store(final Collection<BusinessRule> rules)
    {
        Objects.requireNonNull(rules);

        rules.stream()
            .filter(rule -> rule.getType() == BusinessRuleType.PRECONDITION)
            .forEach(rule -> preconditions.put(rule.getName(), rule));

        rules.stream()
            .filter(rule -> rule.getType() == BusinessRuleType.POST_CONDITION)
            .forEach(rule -> postConditions.put(rule.getName(), rule));
    }

    public Collection<BusinessRule> getPreconditions()
    {
        return Collections.unmodifiableCollection(preconditions.values());
    }

    public Collection<BusinessRule> getPostConditions()
    {
        return Collections.unmodifiableCollection(postConditions.values());
    }

    public Set<BusinessRule> getAllRules()
    {
        Set<BusinessRule> rules = new HashSet<>();
        rules.addAll(preconditions.values());
        rules.addAll(postConditions.values());
        return rules;
    }

}
