package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import lombok.Getter;

import java.io.*;
import java.util.*;

abstract class BusinessRule implements Serializable
{

    @Getter
    protected final String name;

    @Getter
    protected final Expression<Boolean> condition;

    BusinessRule(final String name, final Expression<Boolean> condition)
    {
        this.name = Objects.requireNonNull(name);
        this.condition = Objects.requireNonNull(condition);
    }

}
