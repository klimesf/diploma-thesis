package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;
import cz.filipklimes.diploma.framework.businessContext.Function;

import java.io.*;
import java.util.*;

public class FunctionCall<T> implements Expression<T>, Serializable
{

    private final String methodName;
    private final ExpressionType type;
    private final Expression<?>[] arguments;

    public FunctionCall(final String methodName, final ExpressionType type, final Expression<?>... arguments)
    {
        this.methodName = methodName;
        this.type = type;
        this.arguments = arguments.clone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T interpret(final BusinessOperationContext context)
    {
        Function function = context.getFunction(methodName);

        Object[] calculatedArguments = Arrays.stream(arguments)
            .map(argument -> argument.interpret(context))
            .toArray();

        return (T) type.getUnderlyingClass().cast(function.execute(calculatedArguments));
    }

    @Override
    public Collection<Expression<?>> getArguments()
    {
        return Arrays.asList(arguments);
    }

    @Override
    public Map<String, String> getProperties()
    {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("methodName", methodName);
        propertyMap.put("type", type.getName());
        return propertyMap;
    }

    @Override
    public String getName()
    {
        return "function-call";
    }

    @Override
    public void accept(final ExpressionVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return String.format("call %s()", methodName);
    }

}
