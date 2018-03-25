package cz.filipklimes.diploma.framework.example.ui.business;

import lombok.Getter;
import lombok.Setter;

public class Order
{

    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    private Address billingAddress;

    @Getter
    @Setter
    private Address shippingAddress;

    @Getter
    @Setter
    private String status;

}
