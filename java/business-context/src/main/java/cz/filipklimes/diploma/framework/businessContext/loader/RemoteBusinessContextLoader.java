package cz.filipklimes.diploma.framework.businessContext.loader;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteLoader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RemoteBusinessContextLoader
{

    private final Map<String, RemoteLoader> loaders;

    public RemoteBusinessContextLoader(final Map<String, RemoteLoader> loaders)
    {
        this.loaders = new HashMap<>(loaders);
    }

    public Map<BusinessContextIdentifier, BusinessContext> loadContextsByIdentifier(final Iterable<BusinessContextIdentifier> identifiers)
    {
        Map<String, Set<BusinessContextIdentifier>> identifierByPrefix = new HashMap<>();
        identifiers.forEach(identifier -> identifierByPrefix
            .computeIfAbsent(identifier.getPrefix(), prefix -> new HashSet<>())
            .add(identifier)
        );

        return identifierByPrefix.entrySet()
            .stream()
            .map(this::loadContextsFromRemote)
            .flatMap(Set::stream)
            .collect(Collectors.toMap(
                BusinessContext::getIdentifier,
                Function.identity()
            ));
    }

    private Set<BusinessContext> loadContextsFromRemote(final Map.Entry<String, Set<BusinessContextIdentifier>> entry)
    {
        if (!loaders.containsKey(entry.getKey())) {
            throw new RuntimeException(String.format("No remote origin with name: %s", entry.getKey()));
        }
        return loaders.get(entry.getKey()).loadContexts(entry.getValue());
    }

}
