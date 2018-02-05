package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.exception.UndefinedVariableException;
import lombok.Getter;

import java.util.*;

public class BusinessContext
{

    @Getter
    private final String name;

    /**
     * Variables defined within the business context;
     */
    private final Map<String, Object> variables;

    public BusinessContext(final String name)
    {
        this.name = name;
        this.variables = new HashMap<>();
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

}
