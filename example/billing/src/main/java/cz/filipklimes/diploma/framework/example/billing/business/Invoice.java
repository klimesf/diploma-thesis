package cz.filipklimes.diploma.framework.example.billing.business;

import lombok.Getter;

public class Invoice
{

    @Getter
    private Address billingAddress;

    public Invoice(final Address billingAddress)
    {
        this.billingAddress = billingAddress;
    }

}
