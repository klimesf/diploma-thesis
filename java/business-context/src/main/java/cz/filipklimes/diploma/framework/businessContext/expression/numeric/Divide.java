package cz.filipklimes.diploma.framework.businessContext.expression.numeric;

import cz.filipklimes.diploma.framework.businessContext.expression.BinaryOperator;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionVisitor;
import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

public class Divide extends BinaryOperator<BigDecimal, BigDecimal, BigDecimal>
{

    public Divide(final Expression<BigDecimal> left, final Expression<BigDecimal> right)
    {
        super(left, right);
    }

    @Override
    public BigDecimal interpret(final BusinessOperationContext context)
    {
        return getLeft().interpret(context).divide(getRight().interpret(context), MathContext.DECIMAL128);
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.emptyMap();
    }

    @Override
    public String getName()
    {
        return "numeric-divide";
    }

    @Override
    public void accept(final ExpressionVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return String.format("(%s / %s)", getLeft(), getRight());
    }

}
