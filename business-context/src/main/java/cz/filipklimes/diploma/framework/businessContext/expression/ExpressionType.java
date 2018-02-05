package cz.filipklimes.diploma.framework.businessContext.expression;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

public enum ExpressionType
{

    STRING("string", String.class, Object::toString),
    NUMBER("number", BigDecimal.class, value -> ((BigDecimal) value).toPlainString()),
    BOOL("bool", Boolean.class, Object::toString);

    private static final Map<String, ExpressionType> nameMap = new HashMap<>();

    @Getter
    private final String name;

    @Getter
    private final Class<?> underlyingClass;

    @Getter
    private final Function<?, String> toString;

    static {
        Arrays.stream(ExpressionType.values())
            .forEach(value -> nameMap.put(value.getName(), value));
    }

    ExpressionType(final String name, final Class<?> underlyingClass, final Function<?, String> toString)
    {
        this.name = name;
        this.underlyingClass = underlyingClass;
        this.toString = toString;
    }

    public static ExpressionType of(final String name)
    {
        if (!nameMap.containsKey(name)) {
            throw new RuntimeException(String.format("Unknown ExpressionType name: %s", name));
        }
        return nameMap.get(name);
    }

}
