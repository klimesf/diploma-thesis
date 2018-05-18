package cz.filipklimes.diploma.framework.example.order.facade;

import cz.filipklimes.diploma.framework.businessContext.exception.BusinessRulesCheckFailedException;
import cz.filipklimes.diploma.framework.example.order.business.Product;
import cz.filipklimes.diploma.framework.example.order.business.ShoppingCart;
import cz.filipklimes.diploma.framework.example.order.business.ShoppingCartItem;
import cz.filipklimes.diploma.framework.example.order.business.User;
import cz.filipklimes.diploma.framework.example.order.client.ProductClient;
import cz.filipklimes.diploma.framework.example.order.exception.ProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ShoppingCartFacade
{

    private static Logger log = LoggerFactory.getLogger(ShoppingCartFacade.class);

    private final ProductClient productClient;
    private final ShoppingCartService shoppingCartService;
    private ShoppingCart shoppingCart; // No user dependency or persistence for prototyping purposes

    public ShoppingCartFacade(final ProductClient productClient, final ShoppingCartService shoppingCartService)
    {
        this.productClient = productClient;
        this.shoppingCartService = shoppingCartService;
        this.shoppingCart = new ShoppingCart();
    }

    public List<ShoppingCartItem> listShoppingCartItems()
    {
        return shoppingCart.getItems();
    }

    /**
     * @throws ProductNotFoundException When product with given Id does not exist.
     * @throws BusinessRulesCheckFailedException When business rule check fails.
     */
    public void addProduct(final User user, final Integer productId, final BigDecimal quantity) throws ProductNotFoundException
    {
        Product product = productClient.getProduct(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }

        shoppingCartService.addToShoppingCart(
            user,
            product,
            quantity,
            shoppingCart
        );

        log.info(String.format(
            "Added product %s pieces of product %d into shopping cart, it now has %d items",
            quantity.toPlainString(),
            product.getId(),
            shoppingCart.getItems().size()
        ));
    }

    public void clearCart()
    {
        shoppingCart = new ShoppingCart();
    }

}
