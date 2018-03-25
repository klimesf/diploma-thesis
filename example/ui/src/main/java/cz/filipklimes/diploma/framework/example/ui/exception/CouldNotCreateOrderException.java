package cz.filipklimes.diploma.framework.example.ui.exception;

public class CouldNotCreateOrderException extends Exception
{

    public CouldNotCreateOrderException(final String reason)
    {
        super(reason);
    }

}
