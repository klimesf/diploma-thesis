package cz.filipklimes.diploma.framework.example.ui.facade;

import cz.filipklimes.diploma.framework.example.ui.business.Product;
import cz.filipklimes.diploma.framework.example.ui.client.ProductClient;
import cz.filipklimes.diploma.framework.example.ui.client.ShoppingCartClient;
import cz.filipklimes.diploma.framework.example.ui.client.ShoppingCartClient.ShoppingCartItem;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotAddShoppingCartItemException;
import cz.filipklimes.diploma.framework.example.ui.exception.ProductNotFoundException;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShoppingCartFacade
{

    private final ShoppingCartClient shoppingCartClient;
    private final ProductClient productClient;

    public ShoppingCartFacade(
        final ShoppingCartClient shoppingCartClient,
        final ProductClient productClient
    )
    {
        this.shoppingCartClient = shoppingCartClient;
        this.productClient = productClient;
    }

    public List<Item> listItems() throws ProductNotFoundException
    {
        List<Item> items = new ArrayList<>();
        for (ShoppingCartItem item : shoppingCartClient.listCartItems()) {
            Product product = productClient.getProduct(item.getProductId());
            if (product == null) {
                throw new ProductNotFoundException(item.getProductId());
            }
            items.add(new Item(product.getName(), product.getSellPrice(), item.getQuantity()));
        }
        return items;
    }

    public Product addProduct(final Integer productId) throws CouldNotAddShoppingCartItemException, ProductNotFoundException
    {
        shoppingCartClient.addItem(productId, 1);
        Product product = productClient.getProduct(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }

        return product;
    }

    public static final class Item
    {

        @Getter
        private String productName;

        @Getter
        private Integer productPrice;

        @Getter
        private Integer quantity;

        public Item(final String productName, final Integer productPrice, final Integer quantity)
        {
            this.productName = productName;
            this.productPrice = productPrice;
            this.quantity = quantity;
        }

    }

}
