package cz.filipklimes.diploma.framework.example.order.facade;

import cz.filipklimes.diploma.framework.example.order.business.Address;
import cz.filipklimes.diploma.framework.example.order.business.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderFacade
{

    private static Logger log = LoggerFactory.getLogger(OrderFacade.class);

    private final OrderService orderService;
    private final List<Order> orders; // No persistence for prototyping purposes

    public OrderFacade(
        final OrderService orderService
    )
    {
        this.orderService = orderService;
        this.orders = new ArrayList<>();
    }

    public void createOrder(final int userId)
    {
        Order order = orderService.create(
            null,
            new Address("", "", "", "", ""),
            new Address("", "", "", "", "")
        );
        orders.add(order);
        log.info(String.format("Created a new order %d", order.getId()));
    }

}
