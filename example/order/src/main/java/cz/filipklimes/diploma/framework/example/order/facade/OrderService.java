package cz.filipklimes.diploma.framework.example.order.facade;

import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation;
import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperationParameter;
import cz.filipklimes.diploma.framework.example.order.business.Address;
import cz.filipklimes.diploma.framework.example.order.business.Order;
import cz.filipklimes.diploma.framework.example.order.business.User;
import org.springframework.stereotype.Service;

@Service
public class OrderService
{

    @BusinessOperation("order.create")
    public Order create(
        @BusinessOperationParameter("user") final User user,
        @BusinessOperationParameter("billingAddress") final Address billingAddress,
        @BusinessOperationParameter("shippingAddress") final Address shippingAddress
    )
    {
        return new Order(user, billingAddress, shippingAddress);
    }

}
