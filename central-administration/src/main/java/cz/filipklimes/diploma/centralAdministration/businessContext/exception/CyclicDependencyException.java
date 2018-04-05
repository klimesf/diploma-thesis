package cz.filipklimes.diploma.centralAdministration.businessContext.exception;

public class CyclicDependencyException extends Exception
{

    public CyclicDependencyException()
    {
        super("There is a cyclic dependency within the business context graph");
    }

}
