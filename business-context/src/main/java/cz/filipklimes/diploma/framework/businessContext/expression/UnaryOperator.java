package cz.filipklimes.diploma.framework.businessContext.expression;

import lombok.Getter;

public abstract class UnaryOperator<T, A> implements Expression<T>
{

    @Getter
    private final Expression<A> argument;

    public UnaryOperator(final Expression<A> argument)
    {
        this.argument = argument;
    }

}
