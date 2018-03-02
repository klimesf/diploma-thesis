package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;
import cz.filipklimes.diploma.framework.businessContext.exception.BadObjectPropertyTypeException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedObjectPropertyException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedVariableException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class ObjectPropertyReferenceTest
{

    @Test
    public void testOk()
    {
        BusinessOperationContext context = new BusinessOperationContext("test");
        MockObject object = new MockObject();
        context.setInputParameter("object", object);

        Expression<String> expression = new ObjectPropertyReference<>("object", "property", ExpressionType.STRING);
        String result = expression.interpret(context);

        Assert.assertEquals(object.getProperty(), result);
    }

    @Test(expected = UndefinedObjectPropertyException.class)
    public void testPropertyWithoutGetter()
    {
        BusinessOperationContext context = new BusinessOperationContext("test");
        MockObject object = new MockObject();
        context.setInputParameter("object", object);

        Expression<String> expression = new ObjectPropertyReference<>("object", "hiddenProperty", ExpressionType.STRING);
        expression.interpret(context);
    }

    @Test(expected = BadObjectPropertyTypeException.class)
    public void testBadPropertyType()
    {
        BusinessOperationContext context = new BusinessOperationContext("test");
        MockObject object = new MockObject();
        context.setInputParameter("object", object);

        Expression<BigDecimal> expression = new ObjectPropertyReference<>("object", "property", ExpressionType.NUMBER);
        expression.interpret(context);
    }

    @Test(expected = UndefinedVariableException.class)
    public void testNoVariableWithSuchName()
    {
        BusinessOperationContext context = new BusinessOperationContext("test");
        MockObject object = new MockObject();
        context.setInputParameter("object", object);

        Expression<BigDecimal> expression = new ObjectPropertyReference<>("invalid", "property", ExpressionType.NUMBER);
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
