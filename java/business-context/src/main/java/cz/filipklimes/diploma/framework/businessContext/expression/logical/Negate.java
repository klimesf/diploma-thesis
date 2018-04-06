package cz.filipklimes.diploma.framework.businessContext.expression.logical;

import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionVisitor;
import cz.filipklimes.diploma.framework.businessContext.expression.UnaryOperator;
import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;

import java.util.*;

public class Negate extends UnaryOperator<Boolean, Boolean>
{

    public Negate(final Expression<Boolean> argument)
    {
        super(argument);
    }

    @Override
    public Boolean interpret(final BusinessOperationContext context)
    {
        return !getArgument().interpret(context);
    }

    @Override
    public Map<String, String> getProperties()
    {
        return Collections.emptyMap();
    }

    @Override
    public String getName()
    {
        return "logical-negate";
    }

    @Override
    public void accept(final ExpressionVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public String toString()
    {
        return String.format("not %s", getArgument());
    }

}
