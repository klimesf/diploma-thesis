package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;

import java.io.*;
import java.util.*;

public class IsNotBlank extends UnaryOperator<Boolean, String> implements Serializable
{

    public IsNotBlank(final Expression<String> argument)
    {
        super(argument);
    }

    @Override
    public Boolean interpret(final BusinessOperationContext context)
    {
        String string = getArgument().interpret(context);
        if (string == null) {
            return false;
        }
        return !string.isEmpty();
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.emptyMap();
    }

    @Override
    public String getName()
    {
        return "is-not-blank";
    }

    @Override
    public void accept(final ExpressionVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return String.format("%s is not blank", getArgument());
    }

}
