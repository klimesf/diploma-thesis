package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class IsNotNullTest
{

    @Test
    public void testNotNull()
    {
        BusinessOperationContext context = new BusinessOperationContext("context");
        context.setInputParameter("var", BigDecimal.valueOf(123));
        Expression<Boolean> constant = new IsNotNull<>(new VariableReference<>("var", ExpressionType.NUMBER));
        Assert.assertTrue(constant.interpret(context));
    }

    @Test
    public void testNull()
    {
        BusinessOperationContext context = new BusinessOperationContext("context");
        context.setInputParameter("var", null);
        Expression<Boolean> constant = new IsNotNull<>(new VariableReference<>("var", ExpressionType.NUMBER));
        Assert.assertFalse(constant.interpret(context));
    }

}
