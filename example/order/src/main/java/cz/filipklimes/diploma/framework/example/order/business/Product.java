package cz.filipklimes.diploma.framework.example.order.business;

import lombok.Getter;

public class Product
{

    @Getter
    private Integer id;

    @Getter
    private Integer sellPrice;

    @Getter
    private String name;

    @Getter
    private String description;

    @Getter
    private Boolean hidden = false;

    public Product()
    {
    }

    public Product(final Integer id, final Integer sellPrice, final String name, final String description, final Boolean hidden)
    {
        this.id = id;
        this.sellPrice = sellPrice;
        this.name = name;
        this.description = description;
        this.hidden = hidden;
    }

}
