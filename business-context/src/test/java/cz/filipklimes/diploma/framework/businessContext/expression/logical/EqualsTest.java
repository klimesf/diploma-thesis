package cz.filipklimes.diploma.framework.businessContext.expression.logical;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import org.junit.Assert;
import org.junit.Test;

public class EqualsTest
{

    @Test
    public void testEqualStrings()
    {
        Expression<Boolean> expression = new Equals<>(new Constant<>("string"), new Constant<>("string"));
        Assert.assertTrue(expression.interpret(null));
    }

    @Test
    public void testInequalStrings()
    {
        Expression<Boolean> expression = new Equals<>(new Constant<>("string"), new Constant<>("other"));
        Assert.assertFalse(expression.interpret(null));
    }

    @Test
    public void testEqualIntegers()
    {
        Expression<Boolean> expression = new Equals<>(new Constant<>(123), new Constant<>(123));
        Assert.assertTrue(expression.interpret(null));
    }

    @Test
    public void testInequalIntegers()
    {
        Expression<Boolean> expression = new Equals<>(new Constant<>(123), new Constant<>(321));
        Assert.assertFalse(expression.interpret(null));
    }

    @Test
    public void testMixedTypes()
    {
        Expression<Boolean> expression = new Equals<>(new Constant<>(123), new Constant<>("string"));
        Assert.assertFalse(expression.interpret(null));
    }

}
