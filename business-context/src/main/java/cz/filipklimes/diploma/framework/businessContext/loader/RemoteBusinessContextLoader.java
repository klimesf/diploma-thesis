package cz.filipklimes.diploma.framework.businessContext.loader;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.loader.client.protobuf.ProtobufBusinessContextClient;

import java.util.*;

public class RemoteBusinessContextLoader implements BusinessContextLoader
{

    private final String host;
    private final int port;

    public RemoteBusinessContextLoader(final String host, final int port)
    {
        this.host = host;
        this.port = port;
    }

    @Override
    public Set<BusinessRule> load()
    {
        // TODO: more config
        ProtobufBusinessContextClient client = new ProtobufBusinessContextClient(host, port);
        return client.receiveRules();
    }

}
