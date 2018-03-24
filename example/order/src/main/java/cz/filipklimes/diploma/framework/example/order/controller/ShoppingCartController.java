package cz.filipklimes.diploma.framework.example.order.controller;

import cz.filipklimes.diploma.framework.example.order.business.ShoppingCartItem;
import cz.filipklimes.diploma.framework.example.order.exception.ProductNotFoundException;
import cz.filipklimes.diploma.framework.example.order.facade.ShoppingCartFacade;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class ShoppingCartController
{

    private final ShoppingCartFacade shoppingCartFacade;

    public ShoppingCartController(final ShoppingCartFacade shoppingCartFacade)
    {
        this.shoppingCartFacade = shoppingCartFacade;
    }

    @GetMapping("/shopping-cart")
    public ResponseEntity<?> listShoppingCart()
    {
        List<ShoppingCartItem> shoppingCartItems = shoppingCartFacade.listShoppingCartItems();
        return new ResponseEntity<>(new ListShoppingCartItemsResponse(shoppingCartItems.size(), shoppingCartItems), HttpStatus.OK);
    }

    @PostMapping("/shopping-cart")
    public ResponseEntity<?> addProduct(@RequestBody AddProductToCartRequest request)
    {
        try {
            shoppingCartFacade.addProduct(request.getProductId(), request.getQuantity());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(new AddProductToCartErrorResponse(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public static final class ListShoppingCartItemsResponse
    {

        @Getter
        private final Integer count;

        @Getter
        private final List<ShoppingCartItem> items;

        public ListShoppingCartItemsResponse(final Integer count, final List<ShoppingCartItem> items)
        {
            this.count = count;
            this.items = Collections.unmodifiableList(items);
        }

    }

    public static final class AddProductToCartRequest
    {

        @Getter
        private Integer productId;

        @Getter
        private Integer quantity;

        public AddProductToCartRequest()
        {
        }

        public AddProductToCartRequest(final Integer productId, final Integer quantity)
        {
            this.productId = productId;
            this.quantity = quantity;
        }

    }

    public static final class AddProductToCartErrorResponse
    {

        @Getter
        private final String message;

        private AddProductToCartErrorResponse(final String message)
        {
            this.message = message;
        }

    }

}
