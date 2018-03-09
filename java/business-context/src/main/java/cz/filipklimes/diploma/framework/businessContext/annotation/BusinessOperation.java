package cz.filipklimes.diploma.framework.businessContext.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method which is represents a named business operation.
 * Business rules preconditions should be evaluated before the method
 * is started and business rules post-conditions should be evaluated after
 * the method.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessOperation
{

    /**
     * Name of the business operation unique within the application.
     */
    String value();

}
