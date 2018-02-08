package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;

import java.util.*;

public class VariableAssignment<T> extends UnaryOperator<Void, T>
{

    private final String name;

    public VariableAssignment(final String name, final Expression<T> argument)
    {
        super(argument);
        this.name = name;
    }

    @Override
    public Void interpret(final BusinessContext context)
    {
        T value = getArgument().interpret(context);
        context.setVariable(name, value);
        return null;
    }

    @Override
    public Map<String, String> getProperties()
    {
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("name", name);
        return propertyMap;
    }

    @Override
    public String getName()
    {
        return "variable-assignment";
    }

    @Override
    public String toString()
    {
        return String.format("$%s := %s", name, getArgument());
    }

}
