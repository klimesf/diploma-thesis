package cz.filipklimes.diploma.framework.example.order.facade;

import cz.filipklimes.diploma.framework.example.order.business.Product;
import cz.filipklimes.diploma.framework.example.order.business.ShoppingCart;
import cz.filipklimes.diploma.framework.example.order.business.ShoppingCartItem;
import cz.filipklimes.diploma.framework.example.order.exception.ProductNotFoundException;
import cz.filipklimes.diploma.framework.example.order.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class CartFacade
{

    private final ProductRepository productRepository;
    private final ShoppingCart shoppingCart;

    public CartFacade(final ProductRepository productRepository, final ProductRepository productRepository1)
    {
        this.productRepository = productRepository1;
        this.shoppingCart = new ShoppingCart();
    }

    public void addProduct(final Integer productId, final Integer quantity) throws ProductNotFoundException
    {
        Product product = productRepository.getProduct(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }

        shoppingCart.addItem(new ShoppingCartItem(productId, quantity));
    }

}
