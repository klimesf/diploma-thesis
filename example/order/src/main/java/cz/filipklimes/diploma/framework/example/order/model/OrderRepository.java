package cz.filipklimes.diploma.framework.example.order.model;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository
{

    private final TreeMap<Integer, Order> orders = new TreeMap<>();


    public Collection<Order> findAll()
    {
        return Collections.unmodifiableCollection(orders.values());
    }

    public Order findById(final int orderId)
    {
        return orders.get(orderId);
    }

    public void save(final Order order)
    {
        synchronized (orders) {
            Integer newKey = orders.lastKey() + 1;
            order.setId(newKey);
            orders.put(newKey, order);
        }
    }

}
