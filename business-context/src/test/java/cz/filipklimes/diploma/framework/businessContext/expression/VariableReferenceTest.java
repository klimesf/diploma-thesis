package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedVariableException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class VariableReferenceTest
{

    @Test
    public void testOk()
    {
        BusinessContext context = new BusinessContext("test");
        context.setVariable("number", 123);

        Expression<BigDecimal> expression = new VariableReference<>("number", ExpressionType.NUMBER);
        BigDecimal result = expression.interpret(context);

        Assert.assertEquals(BigDecimal.valueOf(123), result);
    }

    @Test(expected = UndefinedVariableException.class)
    public void testNoVariableWithSuchName()
    {
        BusinessContext context = new BusinessContext("test");
        context.setVariable("number", 123);

        Expression<BigDecimal> expression = new VariableReference<>("invalid", ExpressionType.NUMBER);
        expression.interpret(context);
    }

}
