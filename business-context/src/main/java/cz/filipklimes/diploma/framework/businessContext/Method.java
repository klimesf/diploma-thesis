package cz.filipklimes.diploma.framework.businessContext;

@FunctionalInterface
public interface Method
{

    void execute(Object... args);

}
