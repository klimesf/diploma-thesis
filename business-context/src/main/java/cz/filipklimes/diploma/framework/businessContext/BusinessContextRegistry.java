package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.loader.BusinessContextLoader;

import java.util.*;
import java.util.stream.Collectors;

public final class BusinessContextRegistry
{

    private List<BusinessContextLoader> loaders;

    private BusinessContextRegistry(final List<BusinessContextLoader> loaders)
    {
        this.loaders = Collections.unmodifiableList(loaders);
    }

    public Set<BusinessRule> getAllRules()
    {
        return loaders.stream()
            .map(BusinessContextLoader::load)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public final static class Builder
    {

        private final List<BusinessContextLoader> loaders;

        private Builder()
        {
            this.loaders = new ArrayList<>();
        }

        public Builder addLoader(final BusinessContextLoader loader)
        {
            loaders.add(loader);
            return this;
        }

        public BusinessContextRegistry build()
        {
            return new BusinessContextRegistry(loaders);
        }

    }

}
