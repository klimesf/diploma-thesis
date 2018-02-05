package cz.filipklimes.diploma.framework.businessContext.expression.logical;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import cz.filipklimes.diploma.framework.businessContext.expression.UnaryOperator;

public class Negate extends UnaryOperator<Boolean, Boolean>
{

    public Negate(final Expression<Boolean> argument)
    {
        super(argument);
    }

    @Override
    public Boolean interpret(final BusinessContext context)
    {
        return !getArgument().interpret(context);
    }

}
