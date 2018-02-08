package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class IsNotNullTest
{

    @Test
    public void testNotNull()
    {
        BusinessContext context = new BusinessContext("context");
        context.setVariable("var", BigDecimal.valueOf(123));
        Expression<Boolean> constant = new IsNotNull<>(new VariableReference<>("var", ExpressionType.NUMBER));
        Assert.assertTrue(constant.interpret(context));
    }

    @Test
    public void testNull()
    {
        BusinessContext context = new BusinessContext("context");
        context.setVariable("var", null);
        Expression<Boolean> constant = new IsNotNull<>(new VariableReference<>("var", ExpressionType.NUMBER));
        Assert.assertFalse(constant.interpret(context));
    }

}
