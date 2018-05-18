package cz.filipklimes.diploma.framework.example.ui.exception;

public class CouldNotCreateProductException extends Exception
{

    public CouldNotCreateProductException(final String reason)
    {
        super(reason);
    }

}
