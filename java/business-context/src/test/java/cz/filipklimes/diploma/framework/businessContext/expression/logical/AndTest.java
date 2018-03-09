package cz.filipklimes.diploma.framework.businessContext.expression.logical;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

@RunWith(Parameterized.class)
public class AndTest
{

    private final boolean left;
    private final boolean right;
    private final boolean result;

    public AndTest(final boolean left, final boolean right, final boolean result)
    {
        this.left = left;
        this.right = right;
        this.result = result;
    }

    @Parameterized.Parameters
    public static Collection<Boolean[]> data()
    {
        return Arrays.asList(new Boolean[][]{
            {false, false, false},
            {true, false, false},
            {false, true, false},
            {true, true, true},
        });
    }

    @Test
    public void test()
    {
        Expression<Boolean> expression = new And(new Constant<>(left, ExpressionType.BOOL), new Constant<>(right, ExpressionType.BOOL));
        Assert.assertEquals(result, expression.interpret(null));
    }

}
