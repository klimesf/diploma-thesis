package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;
import cz.filipklimes.diploma.framework.businessContext.exception.BadObjectPropertyTypeException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedObjectPropertyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectPropertyAssignment<T> extends UnaryOperator<Void, T>
{

    private final String objectName;
    private final String propertyName;

    public ObjectPropertyAssignment(final String objectName, final String propertyName, final Expression<T> argument)
    {
        super(argument);
        this.objectName = Objects.requireNonNull(objectName);
        this.propertyName = Objects.requireNonNull(propertyName);
    }

    @Override
    public Void interpret(final BusinessOperationContext context)
    {
        Object object = context.getVariable(objectName);

        Method setter = Arrays.stream(object.getClass().getDeclaredMethods())
            .filter(method -> method.getName().toLowerCase().equals("set" + propertyName.toLowerCase()))
            .findAny()
            .orElseThrow(() -> new UndefinedObjectPropertyException(objectName, propertyName));

        T value = getArgument().interpret(context);

        try {
            setter.invoke(object, value);
            return null;

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not access object property", e);
        } catch (IllegalArgumentException e) {
            throw new BadObjectPropertyTypeException(objectName, propertyName, value.getClass(), e);
        }
    }

    @Override
    public Map<String, String> getProperties()
    {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("objectName", objectName);
        propertyMap.put("propertyName", propertyName);
        return propertyMap;
    }

    @Override
    public String getName()
    {
        return "object-property-assignment";
    }

    @Override
    public String toString()
    {
        return String.format("$%s.%s := %s", objectName, propertyName, getArgument());
    }

}
