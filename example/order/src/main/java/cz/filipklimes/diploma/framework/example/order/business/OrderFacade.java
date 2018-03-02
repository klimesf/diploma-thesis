package cz.filipklimes.diploma.framework.example.order.business;

import cz.filipklimes.diploma.framework.example.order.model.Address;
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

    public void createOrder(final int userId)
    {
        User user = userRepository.findById(userId);
        Order order = orderService.create(
            user,
            new Address("", "", "", "", ""),
            new Address("", "", "", "", "")
        );
        orderRepository.save(order);
    }

}
