package cz.filipklimes.diploma.framework.businessContext;

import lombok.Getter;

public class BusinessRule
{

    @Getter
    private final String name;

    @Getter
    private final String packageName;

    public BusinessRule(final String name, final String packageName)
    {
        this.name = name;
        this.packageName = packageName;
    }

}
