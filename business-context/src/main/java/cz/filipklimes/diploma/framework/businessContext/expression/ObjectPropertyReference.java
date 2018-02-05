package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.exception.BadObjectPropertyTypeException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedObjectPropertyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectPropertyReference<T> implements Terminal<T>
{

    private final String objectName;
    private final String propertyName;
    private final ExpressionType type;

    public ObjectPropertyReference(final String objectName, final String propertyName, final ExpressionType type)
    {
        this.objectName = Objects.requireNonNull(objectName);
        this.propertyName = Objects.requireNonNull(propertyName);
        this.type = Objects.requireNonNull(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T interpret(final BusinessContext context)
    {
        Object object = context.getVariable(objectName);

        Method getter = Arrays.stream(object.getClass().getDeclaredMethods())
            .filter(method -> method.getName().toLowerCase().equals("get" + propertyName))
            .findAny()
            .orElseThrow(() -> new UndefinedObjectPropertyException(objectName, propertyName));

        if (!getter.getReturnType().equals(type.getUnderlyingClass())) {
            throw new BadObjectPropertyTypeException(objectName, propertyName, type.getUnderlyingClass());
        }

        try {
            return (T) type.getUnderlyingClass().cast(getter.invoke(object));

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not access object property", e);
        }
    }

    @Override
    public Map<String, String> getProperties()
    {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("objectName", objectName);
        propertyMap.put("propertyName", propertyName);
        propertyMap.put("type", type.getName());
        return propertyMap;
    }

    @Override
    public String getName()
    {
        return "object-property-reference";
    }

}
