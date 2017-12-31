package cz.filipklimes.diploma.framework.businessContext;

import lombok.Getter;
import lombok.Setter;

public class OrderItem
{

    @Getter
    private final Integer price;

    @Getter
    private final String type;

    @Getter
    @Setter
    private Integer discountedPrice;

    public OrderItem(final Integer price, final String type)
    {
        this.price = price;
        this.type = type;
    }

}
