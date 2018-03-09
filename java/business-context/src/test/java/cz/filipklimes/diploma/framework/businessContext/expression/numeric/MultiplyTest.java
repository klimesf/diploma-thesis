package cz.filipklimes.diploma.framework.businessContext.expression.numeric;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class MultiplyTest
{

    @Test
    public void test()
    {
        Expression<BigDecimal> expression = new Multiply(new Constant<>(BigDecimal.valueOf(2), ExpressionType.NUMBER), new Constant<>(BigDecimal.valueOf(100), ExpressionType.NUMBER));
        BigDecimal result = expression.interpret(null);
        Assert.assertEquals(
            0,
            result.compareTo(BigDecimal.valueOf(200))
        );
    }

}
