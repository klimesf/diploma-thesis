package cz.filipklimes.diploma.framework.businessContext.provider.server.grpc;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleType;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
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
            final BusinessContextProtos.BusinessContextRequestMessage request,
            final StreamObserver<BusinessContextProtos.BusinessContextsResponseMessage> responseObserver
        )
        {
            Set<BusinessContextIdentifier> identifiers = request.getRequiredContextsList().stream()
                .map(BusinessContextIdentifier::new)
                .collect(Collectors.toSet());

            Set<BusinessContextProtos.BusinessContextMessage> contextMessages = registry.getContextByNames(identifiers).values().stream()
                .map(this::buildBusinessContextMessage)
                .collect(Collectors.toSet());

            BusinessContextProtos.BusinessContextsResponseMessage response = BusinessContextProtos.BusinessContextsResponseMessage.newBuilder()
                .addAllContexts(contextMessages)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }

        private BusinessContextProtos.BusinessContextMessage buildBusinessContextMessage(final BusinessContext context)
        {
            return BusinessContextProtos.BusinessContextMessage.newBuilder()
                .setPrefix(context.getIdentifier().getPrefix())
                .setName(context.getIdentifier().getName())
                .addAllIncludedContexts(context.getIncludedContexts().stream()
                    .map(BusinessContextIdentifier::toString)
                    .collect(Collectors.toSet()))
                .addAllPreconditions(context.getPreConditions().stream()
                    .map(this::buildBusinessRuleMessage)
                    .collect(Collectors.toSet()))
                .addAllPostConditions(context.getPostConditions().stream()
                    .map(this::buildBusinessRuleMessage)
                    .collect(Collectors.toSet()))
                .build();
        }

        private BusinessContextProtos.BusinessRuleMessage buildBusinessRuleMessage(final BusinessRule rule)
        {
            return BusinessContextProtos.BusinessRuleMessage.newBuilder()
                .setName(rule.getName())
                .setType(convertType(rule.getType()))
                .setCondition(buildExpression(rule.getCondition()))
                .build();
        }

        private BusinessContextProtos.BusinessRuleType convertType(final BusinessRuleType type)
        {
            switch (type) {
                case PRECONDITION:
                    return BusinessContextProtos.BusinessRuleType.PRECONDITION;
                case POST_CONDITION:
                    return BusinessContextProtos.BusinessRuleType.POST_CONDITION;
                default:
                    return BusinessContextProtos.BusinessRuleType.UNKNOWN;
            }
        }

        private BusinessContextProtos.Expression buildExpression(final Expression<?> expression)
        {
            BusinessContextProtos.Expression.Builder builder = BusinessContextProtos.Expression.newBuilder();
            builder.setName(expression.getName());

            expression.getProperties().forEach((key, value) ->
                builder.addProperties(BusinessContextProtos.ExpressionProperty.newBuilder().setKey(key).setValue(value).build())
            );

            expression.getArguments().stream()
                .map(this::buildExpression)
                .forEach(builder::addArguments);

            return builder.build();
        }

    }

}
