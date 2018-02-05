package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;

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

}
