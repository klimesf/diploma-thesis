package cz.filipklimes.diploma.framework.example.order.model;

import lombok.Getter;

import java.math.BigDecimal;

public class OrderItem
{

    @Getter
    private final BigDecimal price;

    public OrderItem(final BigDecimal price)
    {
        this.price = price;
    }

}
