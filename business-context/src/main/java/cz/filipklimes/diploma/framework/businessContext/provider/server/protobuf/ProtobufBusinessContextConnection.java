package cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.provider.BusinessContextProvider;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.BusinessRulesProtos.BusinessRuleMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.BusinessRulesProtos.BusinessRulesMessage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public final class ProtobufBusinessContextConnection implements Runnable
{

    private final Socket connection;
    private final BusinessContextProvider provider;

    ProtobufBusinessContextConnection(final Socket connection, final BusinessContextProvider provider)
    {
        this.connection = Objects.requireNonNull(connection);
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public void run()
    {
        try (OutputStream out = connection.getOutputStream()) {
            List<BusinessRuleMessage> ruleMessages = provider.getLocalRules().stream()
                .map(this::buildBusinessRuleMessage)
                .collect(Collectors.toList());

            BusinessRulesMessage.newBuilder()
                .addAllRules(ruleMessages)
                .build()
                .writeTo(out);

            out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BusinessRuleMessage buildBusinessRuleMessage(final BusinessRule rule)
    {
        return BusinessRuleMessage.newBuilder()
            .setName(rule.getName())
            .setBusinessContextName(rule.getBusinessContextName())
            .setLeftHandSide(buildExpression(rule.getLeftHandSide()))
            .setRightHandSide(buildExpression(rule.getRightHandSide()))
            .build();
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
