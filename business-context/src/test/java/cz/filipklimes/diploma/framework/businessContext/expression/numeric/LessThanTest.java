package cz.filipklimes.diploma.framework.businessContext.expression.numeric;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class LessThanTest
{

    @Test
    public void testGreater()
    {
        Expression<Boolean> expression = new LessThan(new Constant<>(BigDecimal.ONE), new Constant<>(BigDecimal.ZERO));
        Boolean result = expression.interpret(null);
        Assert.assertFalse(result);
    }

    @Test
    public void testEqual()
    {
        Expression<Boolean> expression = new LessThan(new Constant<>(BigDecimal.ONE), new Constant<>(BigDecimal.ONE));
        Boolean result = expression.interpret(null);
        Assert.assertFalse(result);
    }

    @Test
    public void testLess()
    {
        Expression<Boolean> expression = new LessThan(new Constant<>(BigDecimal.ZERO), new Constant<>(BigDecimal.ONE));
        Boolean result = expression.interpret(null);
        Assert.assertTrue(result);
    }

}
