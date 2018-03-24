package cz.filipklimes.diploma.framework.example.ui.exception;

public class CouldNotAddShoppingCartItemException extends Exception
{

    public CouldNotAddShoppingCartItemException(final String reason)
    {
        super(reason);
    }

}
