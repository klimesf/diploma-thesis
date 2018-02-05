package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;

import java.util.*;

public class Constant<T> implements Terminal<T>
{

    private final T value;

    public Constant(final T value)
    {
        this.value = value;
    }

    @Override
    public T interpret(final BusinessContext context)
    {
        return value;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.singletonMap("value", value.toString());
    }

    @Override
    public String getName()
    {
        return "constant";
    }

}
