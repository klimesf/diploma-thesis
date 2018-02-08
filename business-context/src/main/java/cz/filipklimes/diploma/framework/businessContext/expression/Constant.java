package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;

import java.util.*;

public class Constant<T> implements Terminal<T>
{

    private final T value;
    private final ExpressionType type;

    public Constant(final T value, final ExpressionType type)
    {
        this.value = Objects.requireNonNull(value);
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public T interpret(final BusinessContext context)
    {
        return value;
    }

    @Override
    public Map<String, String> getProperties()
    {
        Map<String, String> properties = new HashMap<>();
        properties.put("value", type.serialize(value));
        properties.put("type", type.getName());
        return properties;
    }

    @Override
    public String getName()
    {
        return "constant";
    }

    @Override
    public String toString()
    {
        return String.format("%s", value);
    }

}
