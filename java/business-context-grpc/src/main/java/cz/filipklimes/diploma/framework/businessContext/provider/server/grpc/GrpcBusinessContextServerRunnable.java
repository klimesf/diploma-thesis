package cz.filipklimes.diploma.framework.businessContext.provider.server.grpc;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.GrpcMessageHelper;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.BusinessContextMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.BusinessContextRequestMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.BusinessContextUpdateRequestMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.BusinessContextsResponseMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.Empty;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextServerGrpc.BusinessContextServerImplBase;

public final class GrpcBusinessContextServerRunnable
{

    private static final Logger logger = Logger.getLogger(GrpcBusinessContextServerRunnable.class.getName());

    private Server server;
    private final BusinessContextRegistry registry;
    private final int port;

    GrpcBusinessContextServerRunnable(final BusinessContextRegistry registry, final int port)
    {
        this.port = port;
        this.registry = Objects.requireNonNull(registry);
    }

    void start() throws IOException
    {
        server = ServerBuilder.forPort(port)
            .addService(new BusinessContextServerImpl(registry))
            .build()
            .start();

        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            GrpcBusinessContextServerRunnable.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    void stop()
    {
        if (server != null) {
            server.shutdown();
        }
    }

    void blockUntilShutdown() throws InterruptedException
    {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class BusinessContextServerImpl extends BusinessContextServerImplBase
    {

        private final BusinessContextRegistry registry;

        public BusinessContextServerImpl(final BusinessContextRegistry registry)
        {
            this.registry = registry;
        }

        @Override
        public void fetchContexts(
            final BusinessContextRequestMessage request,
            final StreamObserver<BusinessContextsResponseMessage> responseObserver
        )
        {
            Set<BusinessContextIdentifier> identifiers = request.getRequiredContextsList().stream()
                .map(BusinessContextIdentifier::parse)
                .collect(Collectors.toSet());

            Set<BusinessContextMessage> contextMessages = registry.getContextsByIdentifiers(identifiers)
                .values().stream()
                .map(GrpcMessageHelper::buildBusinessContextMessage)
                .collect(Collectors.toSet());

            BusinessContextsResponseMessage response = BusinessContextsResponseMessage.newBuilder()
                .addAllContexts(contextMessages)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void fetchAllContexts(final Empty request, final StreamObserver<BusinessContextsResponseMessage> responseObserver)
        {
            Set<BusinessContextMessage> contextMessages = registry.getAllContexts().stream()
                .map(GrpcMessageHelper::buildBusinessContextMessage)
                .collect(Collectors.toSet());

            BusinessContextsResponseMessage response = BusinessContextsResponseMessage.newBuilder()
                .addAllContexts(contextMessages)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        @Override
        public void updateContext(final BusinessContextUpdateRequestMessage request, final StreamObserver<Empty> responseObserver)
        {
            registry.saveOrUpdateBusinessContext(GrpcMessageHelper.buildBusinessContext(request.getContext()));
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }

    }

}
