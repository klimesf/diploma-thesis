package cz.filipklimes.diploma.framework.businessContext.exception;

import lombok.Getter;

public class UndefinedObjectPropertyException extends RuntimeException
{

    @Getter
    private final String objectName;

    @Getter
    private final String propertyName;

    public UndefinedObjectPropertyException(final String objectName, final String propertyName)
    {
        super(String.format("Undefined property %s of object %s", propertyName, objectName));
        this.objectName = objectName;
        this.propertyName = propertyName;
    }

}
