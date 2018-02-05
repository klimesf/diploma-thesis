package cz.filipklimes.diploma.framework.businessContext.expression;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public enum ExpressionType
{

    STRING(
        "string",
        String.class,
        Object::toString,
        value -> value
    ),
    NUMBER(
        "number",
        BigDecimal.class,
        value -> ((BigDecimal) value).toPlainString(),
        BigDecimal::new
        ),
    BOOL(
        "bool",
        Boolean.class,
        Object::toString,
        Boolean::new
        ),
    VOID(
        "void",
        Void.class,
        Object::toString,
        value -> null
    );

    private static final Map<String, ExpressionType> nameMap = new HashMap<>();

    @Getter
    private final String name;

    @Getter
    private final Class<?> underlyingClass;

    @Getter
    private final Function<Object, String> toString;

    @Getter
    private final Function<String, Object> fromString;

    static {
        Arrays.stream(ExpressionType.values())
            .forEach(value -> nameMap.put(value.getName(), value));
    }

    ExpressionType(
        final String name,
        final Class<?> underlyingClass,
        final Function<Object, String> toString,
        final Function<String, Object> fromString
    )
    {
        this.name = name;
        this.underlyingClass = underlyingClass;
        this.toString = toString;
        this.fromString = fromString;
    }

    public String serialize(final Object value)
    {
        return getToString().apply(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialize(final String value)
    {
        return (T) fromString.apply(value);
    }

    public static ExpressionType of(final String name)
    {
        if (!nameMap.containsKey(name)) {
            throw new RuntimeException(String.format("Unknown ExpressionType name: %s", name));
        }
        return nameMap.get(name);
    }

}
