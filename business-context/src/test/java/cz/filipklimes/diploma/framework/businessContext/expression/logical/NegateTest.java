package cz.filipklimes.diploma.framework.businessContext.expression.logical;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import org.junit.Assert;
import org.junit.Test;

public class NegateTest
{

    @Test
    public void test()
    {
        Expression<Boolean> expression = new Negate(new Constant<>(true));
        Assert.assertFalse(expression.interpret(null));
    }

}
