package cz.filipklimes.diploma.framework.example.order.business;

import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation;
import cz.filipklimes.diploma.framework.example.order.model.Order;
import cz.filipklimes.diploma.framework.example.order.model.User;
import org.springframework.stereotype.Service;

@Service
public class OrderService
{

    @BusinessOperation("order.create")
    public Order create(final User user, final String address)
    {
        Order order = new Order();
        return order;
    }

}
