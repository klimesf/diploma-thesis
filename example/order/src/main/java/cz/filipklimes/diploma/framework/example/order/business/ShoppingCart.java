package cz.filipklimes.diploma.framework.example.order.business;

import java.util.*;

public class ShoppingCart
{

    private final List<ShoppingCartItem> items;

    public ShoppingCart()
    {
        this.items = new ArrayList<>();
    }

    public void addItem(final ShoppingCartItem item)
    {
        items.add(item);
    }

    public List<ShoppingCartItem> getItems()
    {
        return Collections.unmodifiableList(items);
    }

}
