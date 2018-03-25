package cz.filipklimes.diploma.framework.example.order.facade;

import cz.filipklimes.diploma.framework.example.order.business.Address;
import cz.filipklimes.diploma.framework.example.order.business.Order;
import cz.filipklimes.diploma.framework.example.order.business.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderFacade
{

    private static Logger log = LoggerFactory.getLogger(OrderFacade.class);

    private final OrderService orderService;
    private final ShoppingCartFacade shoppingCartFacade;
    private int orderIdCounter = 1;
    private final List<Order> orders; // No persistence for prototyping purposes

    public OrderFacade(
        final OrderService orderService,
        final ShoppingCartFacade shoppingCartFacade
    )
    {
        this.orderService = orderService;
        this.shoppingCartFacade = shoppingCartFacade;
        this.orders = new ArrayList<>();
    }

    public List<Order> listOrders(final User user)
    {
        List<Order> result = orderService.listAll(user, this.orders);
        log.info(String.format("Listed all orders by user %s", user.getName()));
        return result;
    }

    public void createOrder(final User user, final Address shippingAddress, final Address billingAddres)
    {
        Order order = orderService.create(
            user,
            user.getEmail(),
            shippingAddress,
            billingAddres
        );
        orders.add(order);
        order.setId(orderIdCounter);
        orderIdCounter++;
        shoppingCartFacade.clearCart();
        log.info(String.format("Created a new order with id %d", order.getId()));
    }

}
