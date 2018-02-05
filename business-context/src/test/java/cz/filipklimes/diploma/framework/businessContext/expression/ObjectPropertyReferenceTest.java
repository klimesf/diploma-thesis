package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.exception.BadObjectPropertyTypeException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedObjectPropertyException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedVariableException;
import org.junit.Assert;
import org.junit.Test;

public class ObjectPropertyReferenceTest
{

    @Test
    public void testOk()
    {
        BusinessContext context = new BusinessContext("test");
        MockObject object = new MockObject();
        context.setVariable("object", object);

        Expression<String> expression = new ObjectPropertyReference<>("object", "property", String.class);
        String result = expression.interpret(context);

        Assert.assertEquals(object.getProperty(), result);
    }

    @Test(expected = UndefinedObjectPropertyException.class)
    public void testPropertyWithoutGetter()
    {
        BusinessContext context = new BusinessContext("test");
        MockObject object = new MockObject();
        context.setVariable("object", object);

        Expression<String> expression = new ObjectPropertyReference<>("object", "hiddenProperty", String.class);
        expression.interpret(context);
    }

    @Test(expected = BadObjectPropertyTypeException.class)
    public void testBadPropertyType()
    {
        BusinessContext context = new BusinessContext("test");
        MockObject object = new MockObject();
        context.setVariable("object", object);

        Expression<Integer> expression = new ObjectPropertyReference<>("object", "property", Integer.class);
        expression.interpret(context);
    }

    @Test(expected = UndefinedVariableException.class)
    public void testNoVariableWithSuchName()
    {
        BusinessContext context = new BusinessContext("test");
        MockObject object = new MockObject();
        context.setVariable("object", object);

        Expression<Integer> expression = new ObjectPropertyReference<>("invalid", "property", Integer.class);
        expression.interpret(context);
    }

    public static final class MockObject
    {

        private final String property = "property";
        private final String hiddenProperty = "hidden property";

        public String getProperty()
        {
            return property;
        }

    }

}
