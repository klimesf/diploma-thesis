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
    private final Class<T> type;

    public ObjectPropertyReference(final String objectName, final String propertyName, final Class<T> type)
    {
        this.objectName = Objects.requireNonNull(objectName);
        this.propertyName = Objects.requireNonNull(propertyName);
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public T interpret(final BusinessContext context)
    {
        Object object = context.getVariable(objectName);

        Method getter = Arrays.stream(object.getClass().getDeclaredMethods())
            .filter(method -> method.getName().toLowerCase().equals("get" + propertyName))
            .findAny()
            .orElseThrow(() -> new UndefinedObjectPropertyException(objectName, propertyName));

        if (!getter.getReturnType().equals(type)) {
            throw new BadObjectPropertyTypeException(objectName, propertyName, type);
        }

        try {
            return type.cast(getter.invoke(object));

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not access object property", e);
        }
    }

}
