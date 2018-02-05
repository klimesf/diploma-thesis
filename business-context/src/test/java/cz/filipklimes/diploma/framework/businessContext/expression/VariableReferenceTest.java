package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedVariableException;
import org.junit.Assert;
import org.junit.Test;

public class VariableReferenceTest
{

    @Test
    public void testOk()
    {
        BusinessContext context = new BusinessContext("test");
        context.setVariable("number", 123);

        Expression<Integer> expression = new VariableReference<>("number", Integer.class);
        Integer result = expression.interpret(context);

        Assert.assertEquals(Integer.valueOf(123), result);
    }

    @Test(expected = UndefinedVariableException.class)
    public void testNoVariableWithSuchName()
    {
        BusinessContext context = new BusinessContext("test");
        context.setVariable("number", 123);

        Expression<Integer> expression = new VariableReference<>("invalid", Integer.class);
        expression.interpret(context);
    }

}
