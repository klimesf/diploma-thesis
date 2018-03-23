package cz.filipklimes.diploma.framework.example.ui.exception;

import lombok.Getter;

public class ProductNotFoundException extends Exception
{

    @Getter
    private final Integer productId;

    public ProductNotFoundException(final Integer productId)
    {
        super(String.format("Product %d not found", productId));
        this.productId = productId;
    }

}
