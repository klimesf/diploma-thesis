package cz.filipklimes.diploma.framework.example.order.business;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class Order implements Entity
{

    @Getter
    @Setter
    private Long id;

    @Getter
    private User user;

    @Getter
    private Address billingAddress;

    @Getter
    private Address shippingAddress;

    private Set<OrderItem> items;

    public Order(final User user, final Address billingAddress, final Address shippingAddress)
    {
        this.user = user;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.items = new HashSet<>();
    }

    public Set<OrderItem> getItems()
    {
        return Collections.unmodifiableSet(items);
    }

}
