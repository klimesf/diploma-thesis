package cz.filipklimes.diploma.framework.businessContext.expression.numeric;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;
import cz.filipklimes.diploma.framework.businessContext.expression.BinaryOperator;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class Subtract extends BinaryOperator<BigDecimal, BigDecimal, BigDecimal>
{

    public Subtract(final Expression<BigDecimal> left, final Expression<BigDecimal> right)
    {
        super(left, right);
    }

    @Override
    public BigDecimal interpret(final BusinessOperationContext context)
    {
        return getLeft().interpret(context).subtract(getRight().interpret(context), MathContext.DECIMAL128);
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.emptyMap();
    }

    @Override
    public String getName()
    {
        return "numeric-subtract";
    }

    @Override
    public String toString()
    {
        return String.format("(%s - %s)", getLeft(), getRight());
    }

}
