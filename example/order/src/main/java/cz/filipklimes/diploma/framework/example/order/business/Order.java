package cz.filipklimes.diploma.framework.example.order.business;

import lombok.Getter;
import lombok.Setter;

public class Order
{

    @Getter
    @Setter
    private Integer id;

    @Getter
    private User user;

    @Getter
    private Address billingAddress;

    @Getter
    private Address shippingAddress;

    @Getter
    @Setter
    private String status;

    public Order(final User user, final Address billingAddress, final Address shippingAddress)
    {
        this.user = user;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.status = "ACCEPTED";
    }

}
