package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class BusinessContextRegistryTest
{

    @Test(expected = RuntimeException.class)
    public void testNoTransaction()
    {
        BusinessContextRegistry registry = createRegistry();
        registry.saveOrUpdateBusinessContext(null);
    }

    @Test
    public void testCommit()
    {
        BusinessContextRegistry registry = createRegistry();
        registry.beginTransaction();
        BusinessContext context = createContext();
        registry.saveOrUpdateBusinessContext(context);
        registry.commitTransaction();
        Assert.assertEquals(1, registry.getAllContexts().size());
        Assert.assertEquals(context.getIdentifier(), registry.getAllContexts().iterator().next().getIdentifier());
    }

    @Test
    public void testRollback()
    {
        BusinessContextRegistry registry = createRegistry();
        registry.beginTransaction();
        BusinessContext context = createContext();
        registry.saveOrUpdateBusinessContext(context);
        registry.rollbackTransaction();
        Assert.assertTrue(registry.getAllContexts().isEmpty());
    }

    private BusinessContextRegistry createRegistry()
    {
        BusinessContextRegistry.Builder builder = BusinessContextRegistry.builder();
        builder.withLocalLoader(Collections::emptySet);
        builder.withRemoteLoader(new RemoteBusinessContextLoader(Collections.emptyMap()));

        return builder.build();
    }

    private BusinessContext createContext()
    {
        return new BusinessContext(
            new BusinessContextIdentifier("user", "validEmail"),
            Collections.emptySet(),
            Collections.emptySet(),
            Collections.emptySet()
        );
    }

}
