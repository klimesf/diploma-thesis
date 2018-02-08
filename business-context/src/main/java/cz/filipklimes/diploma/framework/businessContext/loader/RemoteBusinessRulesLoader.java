package cz.filipklimes.diploma.framework.businessContext.loader;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.loader.client.protobuf.ProtobufBusinessContextClient;

import java.util.*;

public class RemoteBusinessRulesLoader implements BusinessRulesLoader
{

    private final String host;
    private final int port;

    public RemoteBusinessRulesLoader(final String host, final int port)
    {
        this.host = host;
        this.port = port;
    }

    @Override
    public Set<BusinessRule> load()
    {
        ProtobufBusinessContextClient client = new ProtobufBusinessContextClient(host, port);
        return client.receiveRules();
    }

}
