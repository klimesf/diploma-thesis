package cz.filipklimes.diploma.framework.example.order.model;

import lombok.Getter;
import lombok.Setter;

public class Product implements Entity
{

    @Getter
    @Setter
    private Long id;

    @Getter
    private Integer costPrice;

    @Getter
    private Integer sellPrice;

    @Getter
    private Integer stockCount;

    public Product(
        final Long id,
        final Integer costPrice,
        final Integer sellPrice,
        final Integer stockCount
    )
    {
        this.id = id;
        this.costPrice = costPrice;
        this.sellPrice = sellPrice;
        this.stockCount = stockCount;
    }

}
