package cz.filipklimes.diploma.framework.businessContext.expression;

import lombok.Getter;

import java.io.*;
import java.util.*;

public abstract class UnaryOperator<T, A> implements Expression<T>, Serializable
{

    @Getter
    private final Expression<A> argument;

    public UnaryOperator(final Expression<A> argument)
    {
        this.argument = argument;
    }

    @Override
    public Collection<Expression<?>> getArguments()
    {
        return Collections.singletonList(argument);
    }

}
