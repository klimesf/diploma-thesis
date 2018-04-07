package cz.filipklimes.diploma.framework.businessContext;

import java.io.*;

public enum PostConditionType implements Serializable
{

    /**
     * Blanks out objects field if condition evaluates as TRUE.
     */
    FILTER_OBJECT_FIELD,

    /**
     * Removes objects from output list if condition evaluates as TRUE.
     */
    FILTER_LIST_OF_OBJECTS,

    /**
     * Blanks out fields of objects in collection if condition evaluates as TRUE.
     */
    FILTER_LIST_OF_OBJECTS_FIELD;

}
