package cz.filipklimes.diploma.framework.example.order.business;

import lombok.Getter;

import java.math.BigDecimal;

public class ShoppingCartItem
{

    @Getter
    private final Integer productId;

    @Getter
    private final BigDecimal quantity;

    public ShoppingCartItem(final Integer productId, final BigDecimal quantity)
    {
        this.productId = productId;
        this.quantity = quantity;
    }

}
