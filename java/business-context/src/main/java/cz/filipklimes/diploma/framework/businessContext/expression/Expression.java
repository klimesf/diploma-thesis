package cz.filipklimes.diploma.framework.businessContext.expression;

import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;

import java.util.*;

public interface Expression<T>
{

    T interpret(BusinessOperationContext context);

    Collection<Expression<?>> getArguments();

    Map<String, String> getProperties();

    String getName();

}
