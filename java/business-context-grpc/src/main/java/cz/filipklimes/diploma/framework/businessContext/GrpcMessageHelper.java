package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.FunctionCall;
import cz.filipklimes.diploma.framework.businessContext.expression.IsNotBlank;
import cz.filipklimes.diploma.framework.businessContext.expression.IsNotNull;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
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
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.BusinessContextMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.BusinessContextRequestMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.ExpressionMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.ExpressionPropertyMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.PostConditionMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.PostConditionTypeMessage;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.BusinessContextProtos.PreconditionMessage;

import java.util.*;
import java.util.stream.Collectors;

public class GrpcMessageHelper
{

    public static BusinessContextMessage buildBusinessContextMessage(final BusinessContext context)
    {
        return BusinessContextMessage.newBuilder()
            .setPrefix(context.getIdentifier().getPrefix())
            .setName(context.getIdentifier().getName())
            .addAllIncludedContexts(context.getIncludedContexts().stream()
                .map(BusinessContextIdentifier::toString)
                .collect(Collectors.toSet()))
            .addAllPreconditions(context.getPreconditions().stream()
                .map(GrpcMessageHelper::buildPreconditionMessage)
                .collect(Collectors.toSet()))
            .addAllPostConditions(context.getPostConditions().stream()
                .map(GrpcMessageHelper::buildPostConditionMessage)
                .collect(Collectors.toSet()))
            .build();
    }

    public static PreconditionMessage buildPreconditionMessage(final Precondition rule)
    {
        return PreconditionMessage.newBuilder()
            .setName(rule.getName())
            .setCondition(buildExpressionMessage(rule.getCondition()))
            .build();
    }

    public static PostConditionMessage buildPostConditionMessage(final PostCondition rule)
    {
        return PostConditionMessage.newBuilder()
            .setName(rule.getName())
            .setType(convertType(rule.getType()))
            .setReferenceName(rule.getReferenceName())
            .setCondition(buildExpressionMessage(rule.getCondition()))
            .build();
    }

    public static PostConditionTypeMessage convertType(final PostConditionType type)
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

    public static ExpressionMessage buildExpressionMessage(final Expression<?> expression)
    {
        ExpressionMessage.Builder builder = ExpressionMessage.newBuilder();
        builder.setName(expression.getName());

        expression.getProperties().forEach((key, value) ->
            builder.addProperties(ExpressionPropertyMessage.newBuilder().setKey(key).setValue(value).build())
        );

        expression.getArguments().stream()
            .map(GrpcMessageHelper::buildExpressionMessage)
            .forEach(builder::addArguments);

        return builder.build();
    }

    public static BusinessContextRequestMessage buildContextRequest(final Set<BusinessContextIdentifier> identifiers)
    {
        return BusinessContextRequestMessage.newBuilder()
            .addAllRequiredContexts(identifiers.stream()
                .map(BusinessContextIdentifier::toString)
                .collect(Collectors.toSet()))
            .build();
    }

    public static BusinessContext buildBusinessContext(final BusinessContextMessage businessContextMessage)
    {
        return new BusinessContext(
            new BusinessContextIdentifier(businessContextMessage.getPrefix(), businessContextMessage.getName()),
            businessContextMessage.getIncludedContextsList().stream().map(BusinessContextIdentifier::parse).collect(Collectors.toSet()),
            businessContextMessage.getPreconditionsList().stream().map(GrpcMessageHelper::buildPrecondition).collect(Collectors.toSet()),
            businessContextMessage.getPostConditionsList().stream().map(GrpcMessageHelper::buildPostCondition).collect(Collectors.toSet())
        );
    }

    public static Precondition buildPrecondition(final PreconditionMessage message)
    {
        return Precondition.builder()
            .withName(message.getName())
            .withCondition(buildExpression(message.getCondition()))
            .build();
    }

    public static PostCondition buildPostCondition(final PostConditionMessage message)
    {
        return PostCondition.builder()
            .withName(message.getName())
            .withType(convertType(message.getType()))
            .withReferenceName(message.getReferenceName())
            .withCondition(buildExpression(message.getCondition()))
            .build();
    }

    public static PostConditionType convertType(final PostConditionTypeMessage type)
    {
        switch (type) {
            case FILTER_OBJECT_FIELD:
                return PostConditionType.FILTER_OBJECT_FIELD;
            case FILTER_LIST_OF_OBJECTS:
                return PostConditionType.FILTER_LIST_OF_OBJECTS;
            case FILTER_LIST_OF_OBJECTS_FIELD:
                return PostConditionType.FILTER_LIST_OF_OBJECTS_FIELD;
            case UNKNOWN:
            default:
                throw new RuntimeException("Unknown business rule type");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Expression<T> buildExpression(final ExpressionMessage message)
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
                ExpressionType type = ExpressionType.of(findPropertyByName("type", message));
                return (Expression<T>) new Constant<>(type.deserialize(findPropertyByName("value", message)), type);
            case "function-call":
                return (Expression<T>) new FunctionCall<>(
                    findPropertyByName("methodName", message),
                    ExpressionType.of(findPropertyByName("type", message)),
                    message.getArgumentsList().stream()
                        .map(GrpcMessageHelper::buildExpression)
                        .collect(Collectors.toList())
                        .toArray(new Expression[message.getArgumentsCount()])
                );
            case "is-not-null":
                return (Expression<T>) new IsNotNull<>(buildExpression(message.getArguments(0)));
            case "is-not-blank":
                return (Expression<T>) new IsNotBlank(buildExpression(message.getArguments(0)));
            case "object-property-reference":
                return (Expression<T>) new ObjectPropertyReference<>(
                    findPropertyByName("objectName", message),
                    findPropertyByName("propertyName", message),
                    ExpressionType.of(findPropertyByName("type", message))
                );
            case "variable-reference":
                return (Expression<T>) new VariableReference<>(
                    findPropertyByName("name", message),
                    ExpressionType.of(findPropertyByName("type", message))
                );
            default:
                throw new RuntimeException(String.format("Unknown expression: %s", message.getName()));
        }
    }

    public static String findPropertyByName(final String name, final ExpressionMessage message)
    {
        for (ExpressionPropertyMessage expressionProperty : message.getPropertiesList()) {
            if (expressionProperty.getKey().equals(name)) {
                return expressionProperty.getValue();
            }
        }
        return null;
    }

}
