package cz.filipklimes.diploma.framework.example.order.business;

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

    @Getter
    private Boolean hidden;

    public Product(
        final Long id,
        final Integer costPrice,
        final Integer sellPrice,
        final Integer stockCount,
        final Boolean hidden
    )
    {
        this.id = id;
        this.costPrice = costPrice;
        this.sellPrice = sellPrice;
        this.stockCount = stockCount;
        this.hidden = hidden;
    }

}
