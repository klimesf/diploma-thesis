package cz.filipklimes.diploma.framework.example.order.business;

import lombok.Getter;

public class ShoppingCartItem
{

    @Getter
    private final Integer productId;

    @Getter
    private final Integer quantity;

    public ShoppingCartItem(final Integer productId, final Integer quantity)
    {
        this.productId = productId;
        this.quantity = quantity;
    }

}
