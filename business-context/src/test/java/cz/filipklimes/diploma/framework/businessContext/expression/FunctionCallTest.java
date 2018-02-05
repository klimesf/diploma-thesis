package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedFunctionException;
import org.junit.Assert;
import org.junit.Test;

public class FunctionCallTest
{

    @Test
    public void testAddition()
    {
        BusinessContext context = new BusinessContext("test");
        context.addFunction("add", (args) -> (int) args[0] + (int) args[1]);

        Expression<Integer> expression = new FunctionCall<>("add", Integer.class, new Constant<>(1), new Constant<>(2));
        Integer result = expression.interpret(context);

        Assert.assertEquals(Integer.valueOf(3), result);
    }

    @Test(expected = UndefinedFunctionException.class)
    public void testUndefinedFunction()
    {
        BusinessContext context = new BusinessContext("test");

        Expression<Integer> expression = new FunctionCall<>("add", Integer.class, new Constant<>(1), new Constant<>(2));
        expression.interpret(context);
    }

}
