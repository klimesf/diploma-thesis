package cz.filipklimes.diploma.framework.businessContext.exception;

import lombok.Getter;

public class BadObjectPropertyTypeException extends RuntimeException
{

    @Getter
    private final String objectName;

    @Getter
    private final String propertyName;

    @Getter
    private final Class<?> type;

    public BadObjectPropertyTypeException(final String objectName, final String propertyName, final Class<?> type)
    {
        super(String.format("Property %s of object %s is not of type: %s", propertyName, objectName, type.getName()));
        this.objectName = objectName;
        this.propertyName = propertyName;
        this.type = type;
    }

}
