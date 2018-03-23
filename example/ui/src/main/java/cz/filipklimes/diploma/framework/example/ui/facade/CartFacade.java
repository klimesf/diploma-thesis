package cz.filipklimes.diploma.framework.example.ui.facade;

import cz.filipklimes.diploma.framework.example.ui.business.Product;
import cz.filipklimes.diploma.framework.example.ui.exception.ProductNotFoundException;
import cz.filipklimes.diploma.framework.example.ui.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class CartFacade
{

    private final ProductRepository repository;

    public CartFacade(final ProductRepository repository)
    {
        this.repository = repository;
    }

    public Product addProduct(final Integer productId) throws ProductNotFoundException
    {
        Product product = repository.getProduct(productId);
        if (product == null) {
            throw new ProductNotFoundException(productId);
        }

        // TODO: shopping cart logic

        return product;
    }

}
