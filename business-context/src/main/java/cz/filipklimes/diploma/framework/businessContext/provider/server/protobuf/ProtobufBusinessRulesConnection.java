package cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleType;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.BusinessRulesProtos.BusinessRuleMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.BusinessRulesProtos.BusinessRulesMessage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public final class ProtobufBusinessRulesConnection implements Runnable
{

    private final Socket connection;
    private final BusinessContextRegistry registry;

    ProtobufBusinessRulesConnection(final Socket connection, final BusinessContextRegistry registry)
    {
        this.connection = Objects.requireNonNull(connection);
        this.registry = Objects.requireNonNull(registry);
    }

    @Override
    public void run()
    {
        try (OutputStream out = connection.getOutputStream()) {
            Set<BusinessRuleMessage> ruleMessages = registry.getLocalRules().stream()
                .map(this::buildBusinessRuleMessage)
                .collect(Collectors.toSet());

            BusinessRulesMessage.newBuilder()
                .addAllRules(ruleMessages)
                .build()
                .writeTo(out);

            out.flush();

            System.out.println(String.format("Send %d rule messages", ruleMessages.size()));

        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private BusinessRuleMessage buildBusinessRuleMessage(final BusinessRule rule)
    {
        return BusinessRuleMessage.newBuilder()
            .setName(rule.getName())
            .setType(convertType(rule.getType()))
            .addAllApplicableContexts(rule.getApplicableContexts())
            .setCondition(buildExpression(rule.getCondition()))
            .build();
    }

    private BusinessRulesProtos.BusinessRuleType convertType(final BusinessRuleType type)
    {
        switch (type) {
            case PRECONDITION:
                return BusinessRulesProtos.BusinessRuleType.PRECONDITION;
            case POST_CONDITION:
                return BusinessRulesProtos.BusinessRuleType.POST_CONDITION;
            default:
                return BusinessRulesProtos.BusinessRuleType.UNKNOWN;
        }
    }

    private BusinessRulesProtos.Expression buildExpression(final Expression<?> expression)
    {
        BusinessRulesProtos.Expression.Builder builder = BusinessRulesProtos.Expression.newBuilder();
        builder.setName(expression.getName());

        expression.getProperties().forEach(builder::putProperties);

        expression.getArguments().stream()
            .map(this::buildExpression)
            .forEach(builder::addArguments);

        return builder.build();
    }

}
