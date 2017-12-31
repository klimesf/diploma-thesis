package cz.filipklimes.diploma.framework.businessContext;

import lombok.Getter;

import java.io.*;

/**
 * A Business rule represents a single rule in a
 * business context of the application.
 */
public class BusinessRule implements Serializable
{

    private static final long serialVersionUID = 1L;

    /**
     * Name of the rule.
     * Unique within its package.
     */
    @Getter
    private final String name;

    /**
     * Name of the package.
     */
    @Getter
    private final String packageName;

    public BusinessRule(final String name, final String packageName)
    {
        this.name = name;
        this.packageName = packageName;
    }

}
