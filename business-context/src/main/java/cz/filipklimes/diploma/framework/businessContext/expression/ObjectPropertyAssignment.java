package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.exception.BadObjectPropertyTypeException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedObjectPropertyException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectPropertyAssignment<T> implements Expression<Void>
{

    private final String objectName;
    private final String propertyName;
    private final Expression<T> argument;

    public ObjectPropertyAssignment(final String objectName, final String propertyName, final Expression<T> argument)
    {
        this.objectName = Objects.requireNonNull(objectName);
        this.propertyName = Objects.requireNonNull(propertyName);
        this.argument = Objects.requireNonNull(argument);
    }

    @Override
    public Void interpret(final BusinessContext context)
    {
        Object object = context.getVariable(objectName);

        Method setter = Arrays.stream(object.getClass().getDeclaredMethods())
            .filter(method -> method.getName().toLowerCase().equals("set" + propertyName))
            .findAny()
            .orElseThrow(() -> new UndefinedObjectPropertyException(objectName, propertyName));

        T value = argument.interpret(context);

        try {
            setter.invoke(object, value);
            return null;

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Could not access object property", e);
        } catch (IllegalArgumentException e) {
            throw new BadObjectPropertyTypeException(objectName, propertyName, value.getClass(), e);
        }
    }

}
