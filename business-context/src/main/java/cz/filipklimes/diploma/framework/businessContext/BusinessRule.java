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

    /**
     * Name of the business context to which the expression applies.
     */
    @Getter
    private final String businessContextName;

    @Getter
    private final Expression<Boolean> leftHandSide;

    @Getter
    private final Expression<Void> rightHandSide;

    public BusinessRule(
        final String name,
        final String businessContextName,
        final Expression<Boolean> leftHandSide,
        final Expression<Void> rightHandSide
    )
    {
        this.name = Objects.requireNonNull(name);
        this.businessContextName = Objects.requireNonNull(businessContextName);
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
    }

    public static Builder builder()
    {
        return new Builder();
    }


    public static final class Builder
    {

        private String name;
        private String businessContextName;
        private Expression<Boolean> leftHandSide;
        private Expression<Void> rightHandSide;

        private Builder()
        {
        }

        public Builder setName(final String name)
        {
            this.name = name;
            return this;
        }

        public Builder setBusinessContextName(final String businessContextName)
        {
            this.businessContextName = businessContextName;
            return this;
        }

        public Builder setLeftHandSide(final Expression<Boolean> leftHandSide)
        {
            this.leftHandSide = leftHandSide;
            return this;
        }

        public Builder setRightHandSide(final Expression<Void> rightHandSide)
        {
            this.rightHandSide = rightHandSide;
            return this;
        }

        public BusinessRule build()
        {
            return new BusinessRule(
                name,
                businessContextName,
                leftHandSide,
                rightHandSide
            );
        }

    }

}
