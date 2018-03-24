package cz.filipklimes.diploma.framework.example.order.business;

import lombok.Getter;

import java.util.*;

public class User
{

    @Getter
    private Integer id;

    @Getter
    private String name;

    @Getter
    private String email;

    @Getter
    private UserRole role;

    private Set<Order> orders;

    public User()
    {
        this.orders = new HashSet<>();
    }

    public User(final Integer id, final String name, final String email, final UserRole role)
    {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Set<Order> getOrders()
    {
        return Collections.unmodifiableSet(orders);
    }

}
