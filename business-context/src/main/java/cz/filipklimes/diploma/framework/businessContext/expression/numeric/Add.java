package cz.filipklimes.diploma.framework.businessContext.expression.numeric;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.expression.BinaryOperator;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;

import java.math.BigDecimal;
import java.math.MathContext;

public class Add extends BinaryOperator<BigDecimal, BigDecimal, BigDecimal>
{

    public Add(final Expression<BigDecimal> left, final Expression<BigDecimal> right)
    {
        super(left, right);
    }

    @Override
    public BigDecimal interpret(final BusinessContext context)
    {
        return getLeft().interpret(context).add(getRight().interpret(context), MathContext.DECIMAL128);
    }

}
