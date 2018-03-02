package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;

import java.util.*;

public class VariableReference<T> implements Terminal<T>
{

    private final String name;
    private final ExpressionType type;

    public VariableReference(final String name, final ExpressionType type)
    {
        this.name = name;
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T interpret(final BusinessOperationContext context)
    {
        return (T) type.getUnderlyingClass().cast(context.getInputParameter(name));
    }

    @Override
    public Map<String, String> getProperties()
    {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("name", name);
        propertyMap.put("type", type.getName());
        return propertyMap;
    }

    @Override
    public String getName()
    {
        return "variable-reference";
    }

    @Override
    public String toString()
    {
        return String.format("$%s", name);
    }

}
