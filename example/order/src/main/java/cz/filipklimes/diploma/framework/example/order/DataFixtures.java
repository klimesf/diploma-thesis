package cz.filipklimes.diploma.framework.example.order;

import cz.filipklimes.diploma.framework.example.order.business.Address;
import cz.filipklimes.diploma.framework.example.order.business.User;
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
            User user = new User(1, "John Doe", "john.doe@example.com", "CUSTOMER");
            shoppingCartFacade.addProduct(user, 1, 1);
            orderFacade.createOrder(
                user,
                new Address("Czechia", "Prague", "Karlovo Náměstí 5", "15000"),
                new Address("Czechia", "Prague", "Karlovo Náměstí 5", "15000")
            );

            shoppingCartFacade.addProduct(user, 1, 1);

        } catch (ProductNotFoundException e) {
            log.error(String.format("Could not create fixtures: %s", e.getMessage()));
        }
    }

}
