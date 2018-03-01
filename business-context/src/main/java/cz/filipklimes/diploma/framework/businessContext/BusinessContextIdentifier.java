package cz.filipklimes.diploma.framework.businessContext;

import lombok.Getter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusinessContextIdentifier
{

    private static final Pattern PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    private static final Pattern PREFIXED_PATTERN = Pattern.compile("^([a-zA-Z0-9]+)\\.([a-zA-Z0-9]+)$");

    @Getter
    private final String prefix;

    @Getter
    private final String name;

    public BusinessContextIdentifier(final String prefixedName)
    {
        Objects.requireNonNull(prefixedName);
        Matcher matcher = PREFIXED_PATTERN.matcher(prefixedName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid prefixed name for BusinessContextIdentifier");
        }

        this.prefix = matcher.group(1);
        this.name = matcher.group(2);
    }

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
        return Objects.equals(prefix, that.prefix) &&
            Objects.equals(name, that.name);
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

}
