package cz.filipklimes.diploma.framework.businessContext.expression;

import java.util.*;

public interface Terminal<T> extends Expression<T>
{

    @Override
    default Collection<Expression<?>> getArguments()
    {
        return Collections.emptyList();
    }

}
