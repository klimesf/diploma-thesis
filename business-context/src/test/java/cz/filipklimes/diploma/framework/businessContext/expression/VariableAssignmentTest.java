package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import org.junit.Assert;
import org.junit.Test;

public class VariableAssignmentTest
{

    @Test
    public void testNewVariable()
    {
        BusinessContext context = new BusinessContext("test");

        Expression<Void> expression = new VariableAssignment<>("number", new Constant<>(123));
        expression.interpret(context);

        Assert.assertEquals(123, context.getVariable("number"));
    }

    @Test
    public void testExistingVariable()
    {
        BusinessContext context = new BusinessContext("test");
        context.setVariable("number", "prev val");

        Expression<Void> expression = new VariableAssignment<>("number", new Constant<>(123));
        expression.interpret(context);

        Assert.assertEquals(123, context.getVariable("number"));
    }

}
