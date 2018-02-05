package cz.filipklimes.diploma.framework.businessContext.expression.numeric;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.expression.BinaryOperator;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;

import java.math.BigDecimal;
import java.math.MathContext;

public class Subtract extends BinaryOperator<BigDecimal, BigDecimal, BigDecimal>
{

    public Subtract(final Expression<BigDecimal> left, final Expression<BigDecimal> right)
    {
        super(left, right);
    }

    @Override
    public BigDecimal interpret(final BusinessContext context)
    {
        return getLeft().interpret(context).subtract(getRight().interpret(context), MathContext.DECIMAL128);
    }

}
