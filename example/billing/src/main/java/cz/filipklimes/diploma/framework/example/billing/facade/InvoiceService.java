package cz.filipklimes.diploma.framework.example.billing.facade;

import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation;
import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperationParameter;
import cz.filipklimes.diploma.framework.example.billing.business.Address;
import cz.filipklimes.diploma.framework.example.billing.business.Invoice;
import cz.filipklimes.diploma.framework.example.billing.business.User;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService
{

    @BusinessOperation("billing.createInvoice")
    public Invoice create(
        @BusinessOperationParameter("user") final User user,
        @BusinessOperationParameter("billingAddress") final Address billingAddress
    )
    {
        return new Invoice(billingAddress);
    }

}
