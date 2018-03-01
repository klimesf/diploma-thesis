package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedFunctionException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class FunctionCallTest
{

    @Test
    public void testAddition()
    {
        BusinessOperationContext context = new BusinessOperationContext("test");
        context.addFunction("add", (args) -> ((BigDecimal) args[0]).add((BigDecimal) args[1]));

        Expression<BigDecimal> expression = new FunctionCall<>("add", ExpressionType.NUMBER, new Constant<>(BigDecimal.valueOf(1), ExpressionType.NUMBER), new Constant<>(BigDecimal.valueOf(2), ExpressionType.NUMBER));
        BigDecimal result = expression.interpret(context);

        Assert.assertEquals(BigDecimal.valueOf(3), result);
    }

    @Test(expected = UndefinedFunctionException.class)
    public void testUndefinedFunction()
    {
        BusinessOperationContext context = new BusinessOperationContext("test");

        Expression<BigDecimal> expression = new FunctionCall<>("add", ExpressionType.NUMBER, new Constant<>(BigDecimal.valueOf(1), ExpressionType.NUMBER), new Constant<>(BigDecimal.valueOf(2), ExpressionType.NUMBER));
        expression.interpret(context);
    }

}
