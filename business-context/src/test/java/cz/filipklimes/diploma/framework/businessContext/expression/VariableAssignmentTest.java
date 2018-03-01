package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;
import org.junit.Assert;
import org.junit.Test;

public class VariableAssignmentTest
{

    @Test
    public void testNewVariable()
    {
        BusinessOperationContext context = new BusinessOperationContext("test");

        Expression<Void> expression = new VariableAssignment<>("number", new Constant<>(123, ExpressionType.NUMBER));
        expression.interpret(context);

        Assert.assertEquals(123, context.getVariable("number"));
    }

    @Test
    public void testExistingVariable()
    {
        BusinessOperationContext context = new BusinessOperationContext("test");
        context.setVariable("number", "prev val");

        Expression<Void> expression = new VariableAssignment<>("number", new Constant<>(123, ExpressionType.NUMBER));
        expression.interpret(context);

        Assert.assertEquals(123, context.getVariable("number"));
    }

}
