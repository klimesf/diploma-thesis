package cz.filipklimes.diploma.framework.example.order.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class User implements Entity
{

    @Getter
    @Setter
    private Long id;

    @Getter
    private String name;

    @Getter
    private String email;

    @Getter
    private UserRole role;

    private Set<Order> orders;

    public User(final String name, final String email, final UserRole role)
    {
        this.name = name;
        this.email = email;
        this.orders = new HashSet<>();
        this.role = role;
    }

    public Set<Order> getOrders()
    {
        return Collections.unmodifiableSet(orders);
    }

}
