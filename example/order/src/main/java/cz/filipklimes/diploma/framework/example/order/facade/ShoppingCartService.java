package cz.filipklimes.diploma.framework.example.order.facade;

import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation;
import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperationParameter;
import cz.filipklimes.diploma.framework.example.order.business.Product;
import cz.filipklimes.diploma.framework.example.order.business.ShoppingCart;
import cz.filipklimes.diploma.framework.example.order.business.ShoppingCartItem;
import cz.filipklimes.diploma.framework.example.order.business.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShoppingCartService
{

    @BusinessOperation("order.addToShoppingCart")
    public void addToShoppingCart(
        @BusinessOperationParameter("user") final User user,
        @BusinessOperationParameter("product") final Product product,
        @BusinessOperationParameter("quantity") final BigDecimal quantity,
        @BusinessOperationParameter("shoppingCart") final ShoppingCart shoppingCart
    )
    {
        shoppingCart.addItem(new ShoppingCartItem(product.getId(), quantity));
    }

}
