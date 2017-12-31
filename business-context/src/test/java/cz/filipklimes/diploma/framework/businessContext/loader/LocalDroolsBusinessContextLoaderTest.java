package cz.filipklimes.diploma.framework.businessContext.loader;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class LocalDroolsBusinessContextLoaderTest
{

    @Test
    public void test()
    {
        BusinessContextLoader loader = new LocalDroolsBusinessContextLoader();

        Set<BusinessRule> rules = loader.load();

        Assert.assertEquals(1, rules.size());
        BusinessRule rule = rules.iterator().next();
        Assert.assertEquals("generic rule", rule.getName());
        Assert.assertEquals("rules", rule.getPackageName());
    }

}
