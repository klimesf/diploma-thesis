package cz.filipklimes.diploma.framework.example.order;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class Order
{

    @Getter @Setter
    private BigDecimal sum;

    @Getter @Setter
    private BigDecimal discountPercent;

    @Getter @Setter
    private BigDecimal vatPercent;

    public Order(final BigDecimal sum, final BigDecimal discountPercent, final BigDecimal vatPercent)
    {
        this.sum = sum;
        this.discountPercent = discountPercent;
        this.vatPercent = vatPercent;
    }

}
