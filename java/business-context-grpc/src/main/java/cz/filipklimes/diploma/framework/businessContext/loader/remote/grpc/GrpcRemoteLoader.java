package cz.filipklimes.diploma.framework.businessContext.loader.remote.grpc;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteServiceAddress;

import java.util.*;

public class GrpcRemoteLoader implements RemoteLoader
{

    private final RemoteServiceAddress serviceAddress;

    public GrpcRemoteLoader(final RemoteServiceAddress serviceAddress)
    {
        this.serviceAddress = serviceAddress;
    }

    public Set<BusinessContext> loadContexts(final Set<BusinessContextIdentifier> identifiers)
    {
        GrpcBusinessContextClient client = new GrpcBusinessContextClient(serviceAddress);
        try {
            return client.fetchContexts(identifiers);

        } finally {
            try {
                client.shutdown();
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public Set<BusinessContext> loadAllContexts()
    {
        GrpcBusinessContextClient client = new GrpcBusinessContextClient(serviceAddress);
        try {
            return client.fetchAllContexts();

        } finally {
            try {
                client.shutdown();
            } catch (InterruptedException ignored) {
            }
        }
    }

}
