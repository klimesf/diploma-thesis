package cz.filipklimes.diploma.framework.businessContext.expression;

import org.junit.Assert;
import org.junit.Test;

public class ConstantTest
{

    @Test
    public void testOk()
    {
        Expression<Integer> constant = new Constant<>(123, ExpressionType.NUMBER);
        Assert.assertEquals(Integer.valueOf(123), constant.interpret(null));
    }

}
