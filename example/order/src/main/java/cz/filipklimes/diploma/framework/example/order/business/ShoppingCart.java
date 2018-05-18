package cz.filipklimes.diploma.framework.example.order.business;

import java.math.BigDecimal;
import java.util.*;

public class ShoppingCart
{

    private final List<ShoppingCartItem> items;
    private BigDecimal itemCount;

    public ShoppingCart()
    {
        this.items = new ArrayList<>();
        this.itemCount = BigDecimal.ZERO;
    }

    public void addItem(final ShoppingCartItem item)
    {
        items.add(item);
        itemCount = itemCount.add(item.getQuantity());
    }

    public List<ShoppingCartItem> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getItemCount()
    {
        return itemCount;
    }

}
