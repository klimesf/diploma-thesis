package cz.filipklimes.diploma.framework.businessContext;

@FunctionalInterface
public interface Function<T>
{

    T execute(Object... args);

}
