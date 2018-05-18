package cz.filipklimes.diploma.framework.example.billing.facade;

import cz.filipklimes.diploma.framework.example.billing.business.Address;
import cz.filipklimes.diploma.framework.example.billing.business.Invoice;
import cz.filipklimes.diploma.framework.example.billing.business.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InvoiceFacade
{

    private static Logger log = LoggerFactory.getLogger(InvoiceFacade.class);

    private final InvoiceService invoiceService;

    public InvoiceFacade(final InvoiceService invoiceService)
    {
        this.invoiceService = invoiceService;
    }

    public Invoice createInvoice(final User user, final Address billing)
    {
        Invoice invoice = invoiceService.create(user, billing);
        log.info("Created a new invoice");
        return invoice;
    }

}
