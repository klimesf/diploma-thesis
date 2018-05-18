package cz.filipklimes.diploma.framework.example.order.facade;

import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation;
import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperationParameter;
import cz.filipklimes.diploma.framework.example.order.business.Address;
import cz.filipklimes.diploma.framework.example.order.business.Order;
import cz.filipklimes.diploma.framework.example.order.business.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService
{

    @BusinessOperation("order.listAll")
    public List<Order> listAll(
        @BusinessOperationParameter("user") final User user,
        final List<Order> orders
    )
    {
        return orders;
    }

    @BusinessOperation("order.create")
    public Order create(
        @BusinessOperationParameter("user") final User user,
        @BusinessOperationParameter("email") final String email,
        @BusinessOperationParameter("shippingAddress") final Address shippingAddress,
        @BusinessOperationParameter("billingAddress") final Address billingAddress,
        @BusinessOperationParameter("status") final String status
    )
    {
        return new Order(user, billingAddress, shippingAddress, status);
    }

    @BusinessOperation("order.changeStatus")
    public void changeStatus(
        @BusinessOperationParameter("user") final User user,
        @BusinessOperationParameter("order") final Order order,
        @BusinessOperationParameter("status") final String status
    )
    {
        order.setStatus(status);
    }

}
