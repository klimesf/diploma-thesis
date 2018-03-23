package cz.filipklimes.diploma.framework.example.order.controller;

import cz.filipklimes.diploma.framework.example.order.facade.OrderFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController
{

    private final OrderFacade orderFacade;

    @Autowired
    public OrderController(final OrderFacade orderFacade)
    {
        this.orderFacade = orderFacade;
    }

}
