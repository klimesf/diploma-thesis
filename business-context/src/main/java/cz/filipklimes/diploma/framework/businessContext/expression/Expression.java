package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;

import java.util.*;

public interface Expression<T>
{

    T interpret(BusinessContext context);

    Collection<Expression<?>> getArguments();

    Map<String, String> getProperties();

    String getName();

}
