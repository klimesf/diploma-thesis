package cz.filipklimes.diploma.framework.example.order.facade;

import cz.filipklimes.diploma.framework.example.order.business.Address;
import cz.filipklimes.diploma.framework.example.order.business.Order;
import cz.filipklimes.diploma.framework.example.order.repository.OrderRepository;
import cz.filipklimes.diploma.framework.example.order.business.User;
import cz.filipklimes.diploma.framework.example.order.repository.UserRepository;
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
