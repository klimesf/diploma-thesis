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
        this.includedContexts = new HashSet<>(Objects.requireNonNull(includedContexts));
        this.preConditions = new HashSet<>(Objects.requireNonNull(preConditions));
        this.postConditions = new HashSet<>(Objects.requireNonNull(postConditions));
    }

    public Set<BusinessContextIdentifier> getIncludedContexts()
    {
        return Collections.unmodifiableSet(includedContexts);
    }

    public Set<BusinessRule> getPreconditions()
    {
        return Collections.unmodifiableSet(preConditions);
    }

    public Set<BusinessRule> getPostConditions()
    {
        return Collections.unmodifiableSet(postConditions);
    }

    public void merge(final BusinessContext other)
    {
        preConditions.addAll(other.getPreconditions());
        postConditions.addAll(other.getPostConditions());
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private BusinessContextIdentifier identifier;
        private final Set<BusinessContextIdentifier> includedContexts;
        private final Set<BusinessRule> preConditions;
        private final Set<BusinessRule> postConditions;

        private Builder()
        {
            this.includedContexts = new HashSet<>();
            this.preConditions = new HashSet<>();
            this.postConditions = new HashSet<>();
        }

        public Builder withIdentifier(final BusinessContextIdentifier identifier)
        {
            this.identifier = Objects.requireNonNull(identifier);
            return this;
        }

        public Builder withIncludedContext(final BusinessContextIdentifier included)
        {
            this.includedContexts.add(Objects.requireNonNull(included));
            return this;
        }

        public Builder withPrecondition(final BusinessRule rule)
        {
            if (Objects.requireNonNull(rule).getType() != BusinessRuleType.PRECONDITION) {
                throw new IllegalArgumentException("This rule is not a precondition");
            }
            this.preConditions.add(rule);
            return this;
        }

        public Builder withPostCondition(final BusinessRule rule)
        {
            if (Objects.requireNonNull(rule).getType() != BusinessRuleType.POST_CONDITION) {
                throw new IllegalArgumentException("This rule is not a post-condition");
            }
            this.postConditions.add(rule);
            return this;
        }

        public BusinessContext build()
        {
            return new BusinessContext(identifier, includedContexts, preConditions, postConditions);
        }


    }

}
