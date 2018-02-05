package cz.filipklimes.diploma.framework.businessContext.expression.logical;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.expression.BinaryOperator;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;

import java.util.*;

public class Equals<L, R> extends BinaryOperator<Boolean, L, R>
{

    public Equals(final Expression<L> left, final Expression<R> right)
    {
        super(left, right);
    }

    @Override
    public Boolean interpret(final BusinessContext context)
    {
        return getLeft()
            .interpret(context)
            .equals(getRight().interpret(context));
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.emptyMap();
    }

    @Override
    public String getName()
    {
        return "logical-equals";
    }

}
