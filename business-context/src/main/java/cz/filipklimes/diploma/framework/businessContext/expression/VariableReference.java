package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;

public class VariableReference<T> implements Terminal<T>
{

    private final Class<T> type;
    private final String name;

    public VariableReference(final String name, final Class<T> type)
    {
        this.type = type;
        this.name = name;
    }

    @Override
    public T interpret(final BusinessContext context)
    {
        return type.cast(context.getVariable(name));
    }

}
