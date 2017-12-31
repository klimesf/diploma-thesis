package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.loader.LocalDroolsBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.client.protobuf.ProtobufBusinessContextClient;
import cz.filipklimes.diploma.framework.businessContext.provider.BusinessContextProvider;
import cz.filipklimes.diploma.framework.businessContext.provider.DroolsBusinessContextProvider;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.ProtobufBusinessContextServer;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ProtobufBusinessContextExchangeTest
{

    @Test
    public void test()
    {
        BusinessContextProvider provider = new DroolsBusinessContextProvider(new LocalDroolsBusinessContextLoader());
        ProtobufBusinessContextServer server = new ProtobufBusinessContextServer(provider, 5555);
        Thread t = new Thread(server);
        t.start();

        ProtobufBusinessContextClient client = new ProtobufBusinessContextClient("localhost", 5555);
        Set<BusinessRule> rules = client.receiveRules();

        t.interrupt();
        Assert.assertEquals(1, rules.size());
    }

}
