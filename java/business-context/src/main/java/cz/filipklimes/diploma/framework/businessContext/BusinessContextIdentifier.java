package cz.filipklimes.diploma.framework.businessContext;

import lombok.Getter;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusinessContextIdentifier implements Serializable
{

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern PREFIXED_PATTERN = Pattern.compile("^([a-zA-Z0-9]+)\\.([a-zA-Z0-9]+)$");

    @Getter
    private final String prefix;

    @Getter
    private final String name;

    public BusinessContextIdentifier(final String prefix, final String name)
    {
        Objects.requireNonNull(prefix);
        Objects.requireNonNull(name);

        if (!PATTERN.matcher(prefix).matches() || !PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Business context name can contain only alphanumerical characters");
        }

        this.prefix = Objects.requireNonNull(prefix);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BusinessContextIdentifier that = (BusinessContextIdentifier) o;
        return Objects.equals(prefix, that.getPrefix()) &&
            Objects.equals(name, that.getName());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(prefix, name);
    }

    @Override
    public String toString()
    {
        return prefix + "." + name;
    }

    public static BusinessContextIdentifier parse(final String s)
    {
        Objects.requireNonNull(s);
        Matcher matcher = PREFIXED_PATTERN.matcher(s);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Cannot parse BusinessContextIdentifier");
        }

        return new BusinessContextIdentifier(matcher.group(1), matcher.group(2));
    }

}
