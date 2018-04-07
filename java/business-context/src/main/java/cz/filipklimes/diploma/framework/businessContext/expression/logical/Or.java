package cz.filipklimes.diploma.framework.businessContext.expression.logical;

import cz.filipklimes.diploma.framework.businessContext.expression.BinaryOperator;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionVisitor;
import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;

import java.io.*;
import java.util.*;

public final class Or extends BinaryOperator<Boolean, Boolean, Boolean> implements Serializable
{

    public Or(final Expression<Boolean> left, final Expression<Boolean> right)
    {
        super(left, right);
    }

    @Override
    public Boolean interpret(final BusinessOperationContext context)
    {
        return getLeft().interpret(context) || getRight().interpret(context);
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.emptyMap();
    }

    @Override
    public String getName()
    {
        return "logical-or";
    }

    @Override
    public void accept(final ExpressionVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return String.format("%s or %s", getLeft(), getRight());
    }

}
