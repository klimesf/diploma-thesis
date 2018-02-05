package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.exception.MethodAlreadyDefinedException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedMethodException;
import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedVariableException;
import lombok.Getter;

import java.util.*;

public class BusinessContext
{

    @Getter
    private final String name;

    /**
     * Variables defined within the business context.
     */
    private final Map<String, Object> variables;

    /**
     * Methods defined within the business context.
     */
    private final Map<String, Method> methods;

    public BusinessContext(final String name)
    {
        this.name = name;
        this.variables = new HashMap<>();
        this.methods = new HashMap<>();
    }

    /**
     * Returns value of variable with the given name.
     *
     * @param name Name of the variable unique within the context.
     * @return Value of the variable.
     * @throws UndefinedVariableException When name of the variable was not defined within the context.
     */
    public Object getVariable(final String name)
    {
        if (!variables.containsKey(name)) {
            throw new UndefinedVariableException(name);
        }

        return variables.get(name);
    }

    /**
     * Creates or updates variable with the given name to the given value.
     *
     * @param name Name of the variable unique within the context.
     */
    public void setVariable(final String name, final Object value)
    {
        variables.put(name, value);
    }

    /**
     * Returns method with the given name.
     *
     * @param name Name of the method.
     * @return Method with the name.
     * @throws UndefinedMethodException When method with such name was not defined within the context;
     */
    public Method getMethod(final String name)
    {
        if (!methods.containsKey(name)) {
            throw new UndefinedMethodException(name);
        }

        return methods.get(name);
    }

    /**
     * Adds named method to the business context.
     *
     * @param name Name of the method.
     * @param method The method body.
     * @throws MethodAlreadyDefinedException When method with such name was already defined within the context.
     */
    public void addMethod(final String name, final Method method)
    {
        if (methods.containsKey(name)) {
            throw new MethodAlreadyDefinedException(name);
        }
        methods.put(name, method);
    }

}
