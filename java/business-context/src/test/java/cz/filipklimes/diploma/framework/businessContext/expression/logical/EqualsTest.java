package cz.filipklimes.diploma.framework.businessContext.expression.logical;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import org.junit.Assert;
import org.junit.Test;

public class EqualsTest
{

    @Test
    public void testEqualStrings()
    {
        Expression<Boolean> expression = new Equals<>(new Constant<>("string", ExpressionType.STRING), new Constant<>("string", ExpressionType.STRING));
        Assert.assertTrue(expression.interpret(null));
    }

    @Test
    public void testInequalStrings()
    {
        Expression<Boolean> expression = new Equals<>(new Constant<>("string", ExpressionType.STRING), new Constant<>("other", ExpressionType.STRING));
        Assert.assertFalse(expression.interpret(null));
    }

    @Test
    public void testEqualIntegers()
    {
        Expression<Boolean> expression = new Equals<>(new Constant<>(123, ExpressionType.NUMBER), new Constant<>(123, ExpressionType.NUMBER));
        Assert.assertTrue(expression.interpret(null));
    }

    @Test
    public void testInequalIntegers()
    {
        Expression<Boolean> expression = new Equals<>(new Constant<>(123, ExpressionType.NUMBER), new Constant<>(321, ExpressionType.NUMBER));
        Assert.assertFalse(expression.interpret(null));
    }

    @Test
    public void testMixedTypes()
    {
        Expression<Boolean> expression = new Equals<>(new Constant<>(123, ExpressionType.NUMBER), new Constant<>("string", ExpressionType.STRING));
        Assert.assertFalse(expression.interpret(null));
    }

}
