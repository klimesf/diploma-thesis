package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;

public interface Expression<T>
{

    T interpret(BusinessContext context);

}
