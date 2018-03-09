package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;

import java.util.*;

public class IsNotNull<T> extends UnaryOperator<Boolean, T>
{

    public IsNotNull(final Expression<T> argument)
    {
        super(argument);
    }

    @Override
    public Boolean interpret(final BusinessOperationContext context)
    {
        return getArgument().interpret(context) != null;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.emptyMap();
    }

    @Override
    public String getName()
    {
        return "is-not-null";
    }

    @Override
    public String toString()
    {
        return String.format("%s is not null", getArgument());
    }

}
