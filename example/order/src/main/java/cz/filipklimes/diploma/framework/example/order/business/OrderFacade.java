package cz.filipklimes.diploma.framework.example.order.business;

import cz.filipklimes.diploma.framework.businessContext.exception.BusinessRulesCheckFailedException;
import cz.filipklimes.diploma.framework.example.order.model.Order;
import cz.filipklimes.diploma.framework.example.order.model.OrderRepository;
import cz.filipklimes.diploma.framework.example.order.model.User;
import cz.filipklimes.diploma.framework.example.order.model.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderFacade
{

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderFacade(
        final UserRepository userRepository,
        final OrderRepository orderRepository,
        final OrderService orderService
    )
    {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    public void createOrder(final int userId, final String address)
    {
        try {
            User user = userRepository.findUserById(userId);
            Order order = orderService.create(user, address);
            orderRepository.save(order);

        } catch (BusinessRulesCheckFailedException e) {
            System.err.println("Business rules check failed");
        }
    }

}
