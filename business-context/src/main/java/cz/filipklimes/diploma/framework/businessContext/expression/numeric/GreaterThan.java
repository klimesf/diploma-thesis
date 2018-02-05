package cz.filipklimes.diploma.framework.businessContext.expression.numeric;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.expression.BinaryOperator;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;

import java.math.BigDecimal;

public class GreaterThan extends BinaryOperator<Boolean, BigDecimal, BigDecimal>
{

    public GreaterThan(final Expression<BigDecimal> left, final Expression<BigDecimal> right)
    {
        super(left, right);
    }

    @Override
    public Boolean interpret(final BusinessContext context)
    {
        return getLeft().interpret(context).compareTo(getRight().interpret(context)) > 0;
    }

}
