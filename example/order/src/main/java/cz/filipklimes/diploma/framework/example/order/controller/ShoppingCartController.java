package cz.filipklimes.diploma.framework.example.order.controller;

import cz.filipklimes.diploma.framework.businessContext.Precondition;
import cz.filipklimes.diploma.framework.businessContext.exception.BusinessRulesCheckFailedException;
import cz.filipklimes.diploma.framework.example.order.business.ShoppingCartItem;
import cz.filipklimes.diploma.framework.example.order.business.User;
import cz.filipklimes.diploma.framework.example.order.client.UserClient;
import cz.filipklimes.diploma.framework.example.order.controller.response.ErrorResponse;
import cz.filipklimes.diploma.framework.example.order.exception.ProductNotFoundException;
import cz.filipklimes.diploma.framework.example.order.facade.ShoppingCartFacade;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ShoppingCartController
{

    private UserClient userClient;
    private final ShoppingCartFacade shoppingCartFacade;

    public ShoppingCartController(final UserClient userClient, final ShoppingCartFacade shoppingCartFacade)
    {
        this.userClient = userClient;
        this.shoppingCartFacade = shoppingCartFacade;
    }

    @GetMapping("/shopping-cart")
    public ResponseEntity<?> listShoppingCart()
    {
        List<ShoppingCartItem> shoppingCartItems = shoppingCartFacade.listShoppingCartItems();
        return new ResponseEntity<>(new ListShoppingCartItemsResponse(shoppingCartItems.size(), shoppingCartItems), HttpStatus.OK);
    }

    @PostMapping("/shopping-cart")
    public ResponseEntity<?> addProduct(
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestBody AddProductToCartRequest request
    )
    {
        try {
            User user = userId != null ? userClient.getUser(Integer.valueOf(userId)) : null;
            shoppingCartFacade.addProduct(user, request.getProductId(), new BigDecimal(request.getQuantity()));
            return new ResponseEntity<>(HttpStatus.ACCEPTED);

        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);

        } catch (BusinessRulesCheckFailedException e) {
            return new ResponseEntity<>(
                new ErrorResponse(String.format(
                    "Could not add product to cart: %s",
                    e.getFailedRules().stream()
                        .map(Precondition::getName)
                        .collect(Collectors.joining(", "))
                )),
                HttpStatus.UNPROCESSABLE_ENTITY
            );
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

}
