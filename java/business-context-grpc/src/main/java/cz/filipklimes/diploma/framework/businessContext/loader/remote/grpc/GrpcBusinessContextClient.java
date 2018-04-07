package cz.filipklimes.diploma.framework.businessContext.loader.remote.grpc;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.GrpcMessageHelper;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteServiceAddress;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextServerGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class GrpcBusinessContextClient
{

    private static final Logger logger = Logger.getLogger(GrpcBusinessContextClient.class.getName());

    private final ManagedChannel channel;
    private final BusinessContextServerGrpc.BusinessContextServerBlockingStub blockingStub;

    GrpcBusinessContextClient(final RemoteServiceAddress address)
    {
        this.channel = ManagedChannelBuilder.forAddress(address.getHost(), address.getPort())
            .usePlaintext(true)
            .build();
        this.blockingStub = BusinessContextServerGrpc.newBlockingStub(channel);
    }

    void shutdown() throws InterruptedException
    {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public Set<BusinessContext> fetchContexts(final Set<BusinessContextIdentifier> identifiers)
    {
        try {
            return blockingStub.fetchContexts(GrpcMessageHelper.buildContextRequest(identifiers))
                .getContextsList().stream()
                .map(GrpcMessageHelper::buildBusinessContext)
                .collect(Collectors.toSet());

        } catch (StatusRuntimeException e) {
            logger.severe(e.getMessage());
            return Collections.emptySet();
        }
    }

    public Set<BusinessContext> fetchAllContexts()
    {
        try {
            return blockingStub.fetchAllContexts(BusinessContextProtos.Empty.newBuilder().build())
                .getContextsList().stream()
                .map(GrpcMessageHelper::buildBusinessContext)
                .collect(Collectors.toSet());

        } catch (StatusRuntimeException e) {
            logger.severe(e.getMessage());
            return Collections.emptySet();
        }
    }

    public void updateBusinessContext(final BusinessContext context)
    {
        blockingStub.updateContext(BusinessContextProtos.BusinessContextUpdateRequestMessage
            .newBuilder()
            .setContext(GrpcMessageHelper.buildBusinessContextMessage(context))
            .build());
    }

}
