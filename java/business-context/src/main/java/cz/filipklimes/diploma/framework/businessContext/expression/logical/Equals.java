package cz.filipklimes.diploma.framework.businessContext.expression.logical;

import cz.filipklimes.diploma.framework.businessContext.expression.BinaryOperator;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionVisitor;
import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;

import java.util.*;

public class Equals<L, R> extends BinaryOperator<Boolean, L, R>
{

    public Equals(final Expression<L> left, final Expression<R> right)
    {
        super(left, right);
    }

    @Override
    public Boolean interpret(final BusinessOperationContext context)
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

    @Override
    public void accept(final ExpressionVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return String.format("%s equals %s", getLeft(), getRight());
    }

}
