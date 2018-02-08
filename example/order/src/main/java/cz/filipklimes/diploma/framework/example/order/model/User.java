package cz.filipklimes.diploma.framework.example.order.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

public class User
{

    @Getter @Setter
    private int id;

    @Getter
    private final String name;

    @Getter
    private final String email;

    @Getter
    private final String country;

    @Getter
    private final BigDecimal age;

    private final Set<Order> orders;

    public User(final String name, final String email, final String country, final BigDecimal age)
    {
        this(name, email, country, age, new HashSet<>());
    }

    public User(final String name, final String email, final String country, final BigDecimal age, final Set<Order> orders)
    {
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.country = Objects.requireNonNull(country);
        this.age = Objects.requireNonNull(age);
        this.orders = Objects.requireNonNull(orders);
    }

    public Set<Order> getOrders()
    {
        return Collections.unmodifiableSet(orders);
    }

    public void addOrder(final Order order)
    {
        orders.add(Objects.requireNonNull(order));
    }

}
