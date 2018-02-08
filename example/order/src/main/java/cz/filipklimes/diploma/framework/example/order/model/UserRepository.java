package cz.filipklimes.diploma.framework.example.order.model;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository
{

    private final TreeMap<Integer, User> users = new TreeMap<>();

    public Collection<User> findAll()
    {
        return users.values();
    }

    public User findUserById(final int userId)
    {
        return users.get(userId);
    }

    public void save(final User user)
    {
        synchronized (users) {
            Integer newKey = users.lastKey() + 1;
            user.setId(newKey);
            users.put(newKey, user);
        }
    }

}
