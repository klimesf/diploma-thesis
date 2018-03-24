package cz.filipklimes.diploma.framework.example.ui.facade;

import cz.filipklimes.diploma.framework.example.ui.business.Product;
import cz.filipklimes.diploma.framework.example.ui.client.ProductClient;
import cz.filipklimes.diploma.framework.example.ui.client.ShoppingCartClient;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotAddShoppingCartItemException;
import cz.filipklimes.diploma.framework.example.ui.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

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

    public Product addProduct(final Integer productId) throws CouldNotAddShoppingCartItemException, ProductNotFoundException
    {
        shoppingCartClient.addItem(productId, 1);
        Product product = productClient.getProduct(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }

        return product;
    }

}
