package cz.filipklimes.diploma.framework.businessContext;

import lombok.Getter;

import java.util.*;

public class BusinessContext
{

    @Getter
    private final BusinessContextIdentifier identifier;

    private final Set<BusinessContextIdentifier> includedContexts;

    private final Set<BusinessRule> preConditions;

    private final Set<BusinessRule> postConditions;

    public BusinessContext(
        final BusinessContextIdentifier identifier,
        final Set<BusinessContextIdentifier> includedContexts,
        final Set<BusinessRule> preConditions,
        final Set<BusinessRule> postConditions
    )
    {
        this.identifier = identifier;
        this.includedContexts = new HashSet<>(includedContexts);
        this.preConditions = new HashSet<>(preConditions);
        this.postConditions = new HashSet<>(postConditions);
    }

    public Set<BusinessContextIdentifier> getIncludedContexts()
    {
        return Collections.unmodifiableSet(includedContexts);
    }

    public Set<BusinessRule> getPreConditions()
    {
        return Collections.unmodifiableSet(preConditions);
    }

    public Set<BusinessRule> getPostConditions()
    {
        return Collections.unmodifiableSet(postConditions);
    }

    public void merge(final BusinessContext other)
    {
        preConditions.addAll(other.getPreConditions());
        postConditions.addAll(other.getPostConditions());
    }

}
