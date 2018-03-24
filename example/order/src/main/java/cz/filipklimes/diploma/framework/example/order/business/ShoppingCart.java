package cz.filipklimes.diploma.framework.example.order.business;

import java.util.*;

public class ShoppingCart
{

    private final List<ShoppingCartItem> items;
    private Integer itemCount;

    public ShoppingCart()
    {
        this.items = new ArrayList<>();
        this.itemCount = 0;
    }

    public void addItem(final ShoppingCartItem item)
    {
        items.add(item);
        itemCount += item.getQuantity();
    }

    public List<ShoppingCartItem> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public Integer getItemCount()
    {
        return itemCount;
    }

}
