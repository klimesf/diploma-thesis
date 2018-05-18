package cz.filipklimes.diploma.framework.example.ui.facade;

import cz.filipklimes.diploma.framework.example.ui.business.Product;
import cz.filipklimes.diploma.framework.example.ui.client.ProductClient;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotChangePriceException;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotChangeStockException;
import org.springframework.stereotype.Service;

@Service
public class ProductFacade
{

    private final ProductClient productClient;

    public ProductFacade(final ProductClient productClient)
    {
        this.productClient = productClient;
    }

    public Product changePrice(final Integer productId, final String costPrice, final String sellPrice) throws CouldNotChangePriceException
    {
        return productClient.changePrice(productId, costPrice, sellPrice);
    }

    public Product changeStock(final Integer productId, final String stockCount) throws CouldNotChangeStockException
    {
        return productClient.changeStock(productId, stockCount);
    }

}
