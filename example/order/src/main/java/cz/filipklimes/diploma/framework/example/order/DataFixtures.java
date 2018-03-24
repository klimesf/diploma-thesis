package cz.filipklimes.diploma.framework.example.order;

import cz.filipklimes.diploma.framework.example.order.exception.ProductNotFoundException;
import cz.filipklimes.diploma.framework.example.order.facade.OrderFacade;
import cz.filipklimes.diploma.framework.example.order.facade.ShoppingCartFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DataFixtures
{

    private static Logger log = LoggerFactory.getLogger(DataFixtures.class);

    private final OrderFacade orderFacade;
    private final ShoppingCartFacade shoppingCartFacade;

    public DataFixtures(final OrderFacade orderFacade, final ShoppingCartFacade shoppingCartFacade)
    {
        this.orderFacade = orderFacade;
        this.shoppingCartFacade = shoppingCartFacade;
        this.createFixtures();
    }

    private void createFixtures()
    {
        try {
            shoppingCartFacade.addProduct(1, 1);
            // TODO: create order

        } catch (ProductNotFoundException e) {
            log.error(String.format("Could not create fixtures: %s", e.getMessage()));
        }
    }

}
