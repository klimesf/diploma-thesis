package cz.filipklimes.diploma.framework.example.ui.exception;

public class CouldNotCreateInvoiceException extends Exception
{

    public CouldNotCreateInvoiceException(final String reason)
    {
        super(reason);
    }

}
