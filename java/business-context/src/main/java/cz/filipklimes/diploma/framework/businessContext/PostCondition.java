package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.expression.Expression;
import lombok.Getter;

import java.io.*;
import java.util.*;

public class PostCondition extends BusinessRule implements Serializable
{

    @Getter
    private final PostConditionType type;

    @Getter
    private final String referenceName;

    PostCondition(
        final String name,
        final PostConditionType type,
        final String referenceName,
        final Expression<Boolean> condition
    )
    {
        super(name, condition);
        this.type = Objects.requireNonNull(type);
        this.referenceName = Objects.requireNonNull(referenceName);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {

        private String name;
        private PostConditionType type;
        private String referenceName;
        private Expression<Boolean> condition;

        private Builder()
        {
        }

        public Builder withName(final String name)
        {
            this.name = Objects.requireNonNull(name);
            return this;
        }

        public Builder withType(final PostConditionType type)
        {
            this.type = Objects.requireNonNull(type);
            return this;
        }

        public Builder withReferenceName(final String referenceName)
        {
            this.referenceName = Objects.requireNonNull(referenceName);
            return this;
        }

        public Builder withCondition(final Expression<Boolean> condition)
        {
            this.condition = Objects.requireNonNull(condition);
            return this;
        }

        public PostCondition build()
        {
            return new PostCondition(
                name,
                type,
                referenceName,
                condition
            );
        }

    }

}
