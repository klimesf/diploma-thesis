package cz.filipklimes.diploma.framework.businessContext.expression.numeric;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class DivideTest
{

    @Test
    public void test()
    {
        Expression<BigDecimal> expression = new Divide(new Constant<>(BigDecimal.valueOf(100)), new Constant<>(BigDecimal.valueOf(2)));
        BigDecimal result = expression.interpret(null);
        Assert.assertEquals(
            0,
            result.compareTo(BigDecimal.valueOf(50))
        );
    }

}
