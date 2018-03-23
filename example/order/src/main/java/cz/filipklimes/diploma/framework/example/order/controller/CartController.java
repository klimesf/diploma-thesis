package cz.filipklimes.diploma.framework.example.order.controller;

import cz.filipklimes.diploma.framework.example.order.exception.ProductNotFoundException;
import cz.filipklimes.diploma.framework.example.order.facade.CartFacade;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController
{

    private final CartFacade cartFacade;

    public CartController(final CartFacade cartFacade)
    {
        this.cartFacade = cartFacade;
    }

    @PostMapping("/cart")
    public ResponseEntity<?> addProduct(@RequestBody AddProductRequest request)
    {
        try {
            cartFacade.addProduct(request.getProductId(), request.getQuantity());
            return new ResponseEntity<>(new AddProductResponse(), HttpStatus.ACCEPTED);
        } catch (ProductNotFoundException e) {
            throw new RuntimeException("Product not found");
            // return new ResponseEntity<>(new ErrorResponse("Product not found"));
        }
    }

    public static final class AddProductRequest
    {

        @Getter
        private Integer productId;

        @Getter
        private Integer quantity;

        public AddProductRequest()
        {
        }

        public AddProductRequest(final Integer productId, final Integer quantity)
        {
            this.productId = productId;
            this.quantity = quantity;
        }

    }

    public static final class AddProductResponse
    {

    }

}
