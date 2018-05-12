package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;
import org.junit.Assert;
import org.junit.Test;

public class IsNotBlankTest
{

    @Test
    public void testNotBlank()
    {
        BusinessOperationContext context = new BusinessOperationContext("context");
        context.setInputParameter("var", "Hello");
        Expression<Boolean> constant = new IsNotBlank(new VariableReference<>("var", ExpressionType.STRING));
        Assert.assertTrue(constant.interpret(context));
    }

    @Test
    public void testBlank()
    {
        BusinessOperationContext context = new BusinessOperationContext("context");
        context.setInputParameter("var", "");
        Expression<Boolean> constant = new IsNotBlank(new VariableReference<>("var", ExpressionType.STRING));
        Assert.assertFalse(constant.interpret(context));
    }

    @Test
    public void testNull()
    {
        BusinessOperationContext context = new BusinessOperationContext("context");
        context.setInputParameter("var", null);
        Expression<Boolean> constant = new IsNotBlank(new VariableReference<>("var", ExpressionType.STRING));
        Assert.assertFalse(constant.interpret(context));
    }

}
