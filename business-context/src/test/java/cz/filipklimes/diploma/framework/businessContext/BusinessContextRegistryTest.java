package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.loader.LocalDroolsBusinessContextLoader;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class BusinessContextRegistryTest
{

    @Test
    public void test()
    {
        BusinessRule testRule = new BusinessRule("test rule", "rules");
        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .addLoader(new LocalDroolsBusinessContextLoader())
            .addLoader(() -> Collections.singleton(testRule))
            .build();

        Set<BusinessRule> rules = registry.getAllRules();

        Assert.assertEquals(2, rules.size());
        Assert.assertTrue(rules.contains(testRule));
    }

}
