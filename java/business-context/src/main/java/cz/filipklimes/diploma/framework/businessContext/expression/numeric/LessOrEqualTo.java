package cz.filipklimes.diploma.framework.businessContext.expression.numeric;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;
import cz.filipklimes.diploma.framework.businessContext.expression.BinaryOperator;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;

import java.math.BigDecimal;
import java.util.*;

public class LessOrEqualTo extends BinaryOperator<Boolean, BigDecimal, BigDecimal>
{

    public LessOrEqualTo(final Expression<BigDecimal> left, final Expression<BigDecimal> right)
    {
        super(left, right);
    }

    @Override
    public Boolean interpret(final BusinessOperationContext context)
    {
        return getLeft().interpret(context).compareTo(getRight().interpret(context)) <= 0;
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.emptyMap();
    }

    @Override
    public String getName()
    {
        return "numeric-lte";
    }

    @Override
    public String toString()
    {
        return String.format("%s <= %s", getLeft(), getRight());
    }

}