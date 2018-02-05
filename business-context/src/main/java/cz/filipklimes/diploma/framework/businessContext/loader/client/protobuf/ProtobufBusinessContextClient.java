package cz.filipklimes.diploma.framework.businessContext.loader.client.protobuf;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.FunctionCall;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyAssignment;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.VariableAssignment;
import cz.filipklimes.diploma.framework.businessContext.expression.VariableReference;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.And;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.Equals;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.Negate;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.Or;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Add;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Divide;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.GreaterOrEqualTo;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.GreaterThan;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.LessOrEqualTo;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.LessThan;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Multiply;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Subtract;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.BusinessRulesProtos;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class ProtobufBusinessContextClient
{

    private final String host;
    private final int port;

    public ProtobufBusinessContextClient(final String host, final int port)
    {
        this.host = host;
        this.port = port;
    }

    /**
     * Recieves rules from protobuf business context server.
     *
     * @return Set of business rules.
     */
    public Set<BusinessRule> receiveRules()
    {
        try (
            Socket socket = new Socket(host, port);
            InputStream in = socket.getInputStream()
        ) {
            return BusinessRulesProtos.BusinessRulesMessage.parseFrom(in)
                .getRulesList().stream()
                .map(this::buildBusinessRule)
                .collect(Collectors.toSet());

        } catch (IOException e) {
            throw new RuntimeException("Could not load business rules", e);
        }
    }

    private BusinessRule buildBusinessRule(final BusinessRulesProtos.BusinessRuleMessage message)
    {
        BusinessRule.Builder builder = BusinessRule.builder();
        builder.setName(message.getName())
            .setBusinessContextName(message.getBusinessContextName());

        builder.setLeftHandSide(buildExpression(message.getLeftHandSide()));
        builder.setRightHandSide(buildExpression(message.getRightHandSide()));

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private <T> Expression<T> buildExpression(final BusinessRulesProtos.Expression message)
    {
        switch (message.getName()) {
            case "logical-and":
                return (Expression<T>) new And(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "logical-equals":
                return (Expression<T>) new Equals(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "logical-negate":
                return (Expression<T>) new Negate(buildExpression(message.getArguments(0)));
            case "logical-or":
                return (Expression<T>) new Or(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "numeric-add":
                return (Expression<T>) new Add(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "numeric-divide":
                return (Expression<T>) new Divide(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "numeric-gte":
                return (Expression<T>) new GreaterOrEqualTo(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "numeric-gt":
                return (Expression<T>) new GreaterThan(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "numeric-lte":
                return (Expression<T>) new LessOrEqualTo(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "numeric-lt":
                return (Expression<T>) new LessThan(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "numeric-multiply":
                return (Expression<T>) new Multiply(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "numeric-subtract":
                return (Expression<T>) new Subtract(buildExpression(message.getArguments(0)), buildExpression(message.getArguments(1)));
            case "constant":
                ExpressionType type = ExpressionType.of(message.getPropertiesMap().get("type"));
                return (Expression<T>) new Constant<>(type.deserialize(message.getPropertiesMap().get("value")), type);
            case "function-call":
                return (Expression<T>) new FunctionCall<>(
                    message.getPropertiesMap().get("methodName"),
                    ExpressionType.of(message.getPropertiesMap().get("type")),
                    (Expression<?>[]) message.getArgumentsList().stream().map(this::buildExpression).toArray()
                );
            case "object-property-assignment":
                return (Expression<T>) new ObjectPropertyAssignment<>(
                    message.getPropertiesMap().get("objectName"),
                    message.getPropertiesMap().get("propertyName"),
                    buildExpression(message.getArguments(0))
                );
            case "object-property-reference":
                return (Expression<T>) new ObjectPropertyReference<>(
                    message.getPropertiesMap().get("objectName"),
                    message.getPropertiesMap().get("propertyName"),
                    ExpressionType.of(message.getPropertiesMap().get("type"))
                );
            case "variable-assignment":
                return (Expression<T>) new VariableAssignment<>(
                    message.getPropertiesMap().get("name"),
                    buildExpression(message.getArguments(0))
                );
            case "variable-reference":
                return (Expression<T>) new VariableReference<>(
                    message.getPropertiesMap().get("name"),
                    ExpressionType.of(message.getPropertiesMap().get("type"))
                );
            default:
                throw new RuntimeException(String.format("Unknown expression: %s", message.getName()));
        }
    }

}
