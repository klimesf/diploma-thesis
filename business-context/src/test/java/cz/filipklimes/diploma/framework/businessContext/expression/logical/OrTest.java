package cz.filipklimes.diploma.framework.businessContext.expression.logical;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.*;

@RunWith(Parameterized.class)
public class OrTest
{

    private final boolean left;
    private final boolean right;
    private final boolean result;

    public OrTest(final boolean left, final boolean right, final boolean result)
    {
        this.left = left;
        this.right = right;
        this.result = result;
    }

    @Parameters
    public static Collection<Boolean[]> data()
    {
        return Arrays.asList(new Boolean[][]{
            {false, false, false},
            {true, false, true},
            {false, true, true},
            {true, true, true},
        });
    }

    @Test
    public void test()
    {
        Expression<Boolean> expression = new Or(new Constant<>(left), new Constant<>(right));
        Assert.assertEquals(result, expression.interpret(null));
    }

}
