package cz.filipklimes.diploma.framework.businessContext.provider.server.grpc;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.PostCondition;
import cz.filipklimes.diploma.framework.businessContext.PostConditionType;
import cz.filipklimes.diploma.framework.businessContext.Precondition;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.BusinessContextMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.BusinessContextRequestMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.BusinessContextsResponseMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.ExpressionMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.ExpressionPropertyMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.PostConditionMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.PostConditionTypeMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.PreconditionMessage;
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
                .map(this::buildBusinessContextMessage)
                .collect(Collectors.toSet());

            BusinessContextsResponseMessage response = BusinessContextsResponseMessage.newBuilder()
                .addAllContexts(contextMessages)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        private BusinessContextMessage buildBusinessContextMessage(final BusinessContext context)
        {
            return BusinessContextMessage.newBuilder()
                .setPrefix(context.getIdentifier().getPrefix())
                .setName(context.getIdentifier().getName())
                .addAllIncludedContexts(context.getIncludedContexts().stream()
                    .map(BusinessContextIdentifier::toString)
                    .collect(Collectors.toSet()))
                .addAllPreconditions(context.getPreconditions().stream()
                    .map(this::buildPrecondition)
                    .collect(Collectors.toSet()))
                .addAllPostConditions(context.getPostConditions().stream()
                    .map(this::buildPostCondition)
                    .collect(Collectors.toSet()))
                .build();
        }

        private PreconditionMessage buildPrecondition(final Precondition rule)
        {
            return PreconditionMessage.newBuilder()
                .setName(rule.getName())
                .setCondition(buildExpression(rule.getCondition()))
                .build();
        }

        private PostConditionMessage buildPostCondition(final PostCondition rule)
        {
            return PostConditionMessage.newBuilder()
                .setName(rule.getName())
                .setType(convertType(rule.getType()))
                .setReferenceName(rule.getReferenceName())
                .setCondition(buildExpression(rule.getCondition()))
                .build();
        }

        private PostConditionTypeMessage convertType(final PostConditionType type)
        {
            switch (type) {
                case FILTER_OBJECT_FIELD:
                    return PostConditionTypeMessage.FILTER_OBJECT_FIELD;
                case FILTER_LIST_OF_OBJECTS:
                    return PostConditionTypeMessage.FILTER_LIST_OF_OBJECTS;
                case FILTER_LIST_OF_OBJECTS_FIELD:
                    return PostConditionTypeMessage.FILTER_LIST_OF_OBJECTS_FIELD;
                default:
                    return PostConditionTypeMessage.UNKNOWN;
            }
        }

        private ExpressionMessage buildExpression(final Expression<?> expression)
        {
            ExpressionMessage.Builder builder = ExpressionMessage.newBuilder();
            builder.setName(expression.getName());

            expression.getProperties().forEach((key, value) ->
                builder.addProperties(ExpressionPropertyMessage.newBuilder().setKey(key).setValue(value).build())
            );

            expression.getArguments().stream()
                .map(this::buildExpression)
                .forEach(builder::addArguments);

            return builder.build();
        }

    }

}
