package cz.filipklimes.diploma.framework.businessContext.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method parameter which is represents a named business operation parameter.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessOperationParameter
{

    /**
     * Unique name of the business operation parameter.
     */
    String value();

}
