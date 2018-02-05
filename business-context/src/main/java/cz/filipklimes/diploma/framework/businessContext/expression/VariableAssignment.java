package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;

public class VariableAssignment<T> implements Expression<Void>
{

    private final String name;
    private final Expression<T> argument;

    public VariableAssignment(final String name, final Expression<T> argument)
    {
        this.name = name;
        this.argument = argument;
    }

    @Override
    public Void interpret(final BusinessContext context)
    {
        T value = argument.interpret(context);
        context.setVariable(name, value);
        return null;
    }

}
