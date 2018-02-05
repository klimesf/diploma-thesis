package cz.filipklimes.diploma.framework.businessContext.expression.numeric;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class GreaterThanTest
{

    @Test
    public void testGreater()
    {
        Expression<Boolean> expression = new GreaterThan(new Constant<>(BigDecimal.ONE, ExpressionType.NUMBER), new Constant<>(BigDecimal.ZERO, ExpressionType.NUMBER));
        Boolean result = expression.interpret(null);
        Assert.assertTrue(result);
    }

    @Test
    public void testEqual()
    {
        Expression<Boolean> expression = new GreaterThan(new Constant<>(BigDecimal.ONE, ExpressionType.NUMBER), new Constant<>(BigDecimal.ONE, ExpressionType.NUMBER));
        Boolean result = expression.interpret(null);
        Assert.assertFalse(result);
    }

    @Test
    public void testLess()
    {
        Expression<Boolean> expression = new GreaterThan(new Constant<>(BigDecimal.ZERO, ExpressionType.NUMBER), new Constant<>(BigDecimal.ONE, ExpressionType.NUMBER));
        Boolean result = expression.interpret(null);
        Assert.assertFalse(result);
    }

}
