package cz.filipklimes.diploma.framework.example.ui.facade;

import cz.filipklimes.diploma.framework.example.ui.business.User;
import cz.filipklimes.diploma.framework.example.ui.client.UserClient;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotCreateUserException;
import cz.filipklimes.diploma.framework.example.ui.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserFacade
{

    private static Logger log = LoggerFactory.getLogger(UserFacade.class);

    private final UserClient userClient;

    public UserFacade(final UserClient userClient)
    {
        this.userClient = userClient;
    }

    public User getUser(final Integer userId) throws UserNotFoundException
    {
        User user = userClient.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        return user;
    }

    public List<User> listUsers()
    {
        return userClient.listUsers();
    }

    public User register(final String name, final String email) throws CouldNotCreateUserException
    {
        return userClient.register(name, email);
    }

    public User createEmployee(final String name, final String email) throws CouldNotCreateUserException
    {
        return userClient.createEmployee(name, email);
    }

}
