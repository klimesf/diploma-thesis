package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.expression.Expression;

import java.io.*;
import java.util.*;

public class Precondition extends BusinessRule implements Serializable
{

    Precondition(
        final String name,
        final Expression<Boolean> condition
    )
    {
        super(name, condition);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String name;
        private Expression<Boolean> condition;

        private Builder()
        {
        }

        public Builder withName(final String name)
        {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        public Builder withCondition(final Expression<Boolean> condition)
        {
            this.condition = Objects.requireNonNull(condition);
            return this;
        }

        public Precondition build()
        {
            return new Precondition(
                name,
                condition
            );
        }

    }

}
