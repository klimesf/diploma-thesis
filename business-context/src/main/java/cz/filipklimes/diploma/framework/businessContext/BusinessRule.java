package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import lombok.Getter;

import java.io.*;
import java.util.*;

/**
 * A Business expression represents a single expression in a
 * business context of the application.
 */
public class BusinessRule implements Serializable
{

    private static final long serialVersionUID = 1L;

    /**
     * Name of the expression.
     * Unique within its package.
     */
    @Getter
    private final String name;

    @Getter
    private final BusinessRuleType type;

    @Getter
    private final Expression<Boolean> condition;

    public BusinessRule(
        final String name,
        final BusinessRuleType type,
        final Expression<Boolean> condition
    )
    {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.condition = Objects.requireNonNull(condition);
    }

    public static Builder builder()
    {
        return new Builder();
    }


    public static final class Builder
    {

        private String name;
        private BusinessRuleType type;
        private Expression<Boolean> condition;

        private Builder()
        {
        }

        public Builder setName(final String name)
        {
            this.name = name;
            return this;
        }

        public Builder setType(final BusinessRuleType type)
        {
            this.type = Objects.requireNonNull(type);
            return this;
        }

        public Builder setCondition(final Expression<Boolean> condition)
        {
            this.condition = Objects.requireNonNull(condition);
            return this;
        }

        public BusinessRule build()
        {
            return new BusinessRule(
                name,
                type,
                condition
            );
        }

    }

}
