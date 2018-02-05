package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.exception.BadObjectPropertyTypeException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedObjectPropertyException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedVariableException;
import org.junit.Assert;
import org.junit.Test;

public class ObjectPropertyAssignmentTest
{

    @Test
    public void testOk()
    {
        BusinessContext context = new BusinessContext("test");
        MockObject object = new MockObject();
        context.setVariable("object", object);

        Expression<Void> expression = new ObjectPropertyAssignment<>("object", "property", new Constant<>("new value", ExpressionType.STRING));
        expression.interpret(context);

        Assert.assertEquals("new value", ((MockObject) context.getVariable("object")).getProperty());
    }

    @Test(expected = UndefinedObjectPropertyException.class)
    public void testPropertyWithoutSetter()
    {
        BusinessContext context = new BusinessContext("test");
        MockObject object = new MockObject();
        context.setVariable("object", object);

        Expression<Void> expression = new ObjectPropertyAssignment<>("object", "hiddenProperty", new Constant<>("new value", ExpressionType.STRING));
        expression.interpret(context);
    }

    @Test(expected = BadObjectPropertyTypeException.class)
    public void testBadPropertyType()
    {
        BusinessContext context = new BusinessContext("test");
        MockObject object = new MockObject();
        context.setVariable("object", object);

        Expression<Void> expression = new ObjectPropertyAssignment<>("object", "property", new Constant<>(123, ExpressionType.NUMBER));
        expression.interpret(context);
    }

    @Test(expected = UndefinedVariableException.class)
    public void testNoVariableWithSuchName()
    {
        BusinessContext context = new BusinessContext("test");
        MockObject object = new MockObject();
        context.setVariable("object", object);

        Expression<Void> expression = new ObjectPropertyAssignment<>("invalid", "property", new Constant<>("new value", ExpressionType.STRING));
        expression.interpret(context);
    }

    public static final class MockObject
    {

        private String property = "property";
        private String hiddenProperty = "hidden property";

        public String getProperty()
        {
            return property;
        }

        public void setProperty(final String property)
        {
            this.property = property;
        }

    }

}
