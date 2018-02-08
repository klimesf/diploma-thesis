package cz.filipklimes.diploma.framework.example.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

public class Order
{

    @Getter @Setter
    private Integer id;

    @Getter @Setter
    private BigDecimal sum;

    private Set<OrderItem> items;

    public Order()
    {
        this.items = new HashSet<>();
    }

    public void addItem(final OrderItem item)
    {
        Objects.requireNonNull(item);
        items.add(item);
        sum = sum.add(item.getPrice());
    }

    public Set<OrderItem> getItems()
    {
        return Collections.unmodifiableSet(items);
    }

}
