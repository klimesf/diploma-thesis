package cz.filipklimes.diploma.framework.example.billing.controller;

import cz.filipklimes.diploma.framework.businessContext.Precondition;
import cz.filipklimes.diploma.framework.businessContext.exception.BusinessRulesCheckFailedException;
import cz.filipklimes.diploma.framework.example.billing.business.Address;
import cz.filipklimes.diploma.framework.example.billing.business.User;
import cz.filipklimes.diploma.framework.example.billing.controller.response.ErrorResponse;
import cz.filipklimes.diploma.framework.example.billing.facade.InvoiceFacade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
public class InvoiceController
{

    private final InvoiceFacade invoiceFacade;

    public InvoiceController(final InvoiceFacade invoiceFacade)
    {
        this.invoiceFacade = invoiceFacade;
    }

    @PostMapping("/invoices")
    public ResponseEntity<?> createOrder(
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestHeader(value = "X-User-Role", required = false) String userRole,
        @RequestBody CreateInvoiceRequest request
    )
    {
        try {
            invoiceFacade.createInvoice(new User(userId, userRole), request.getBilling());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);

        } catch (BusinessRulesCheckFailedException e) {
            return new ResponseEntity<>(
                new ErrorResponse(String.format(
                    "Could not create invoice: %s",
                    e.getFailedRules().stream()
                        .map(Precondition::getName)
                        .collect(Collectors.joining(", "))
                )),
                HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
    }

    private static final class CreateInvoiceRequest
    {

        @Getter
        @Setter
        private Address billing;

    }

}
