package cz.filipklimes.diploma.framework.businessContext;

import lombok.Getter;

import java.io.*;
import java.util.*;

public class BusinessContext implements Serializable
{

    @Getter
    private final BusinessContextIdentifier identifier;

    private final Set<BusinessContextIdentifier> includedContexts;

    private final Set<Precondition> preConditions;

    private final Set<PostCondition> postConditions;

    public BusinessContext(
        final BusinessContextIdentifier identifier,
        final Set<BusinessContextIdentifier> includedContexts,
        final Set<Precondition> preConditions,
        final Set<PostCondition> postConditions
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

    public Set<Precondition> getPreconditions()
    {
        return Collections.unmodifiableSet(preConditions);
    }

    public Set<PostCondition> getPostConditions()
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
        private final Set<Precondition> preConditions;
        private final Set<PostCondition> postConditions;

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

        public Builder withPrecondition(final Precondition rule)
        {
            this.preConditions.add(rule);
            return this;
        }

        public Builder withPostCondition(final PostCondition rule)
        {
            this.postConditions.add(rule);
            return this;
        }

        public BusinessContext build()
        {
            return new BusinessContext(identifier, includedContexts, preConditions, postConditions);
        }

    }

}
