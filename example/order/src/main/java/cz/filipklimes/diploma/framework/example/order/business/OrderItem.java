package cz.filipklimes.diploma.framework.example.order.business;

import lombok.Getter;

public class OrderItem
{

    @Getter
    private Integer count;

    @Getter
    private Product product;

    public OrderItem(final Integer count)
    {
        this.count = count;
    }

}
