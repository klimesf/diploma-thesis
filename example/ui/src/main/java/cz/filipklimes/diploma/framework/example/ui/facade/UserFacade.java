package cz.filipklimes.diploma.framework.example.ui.facade;

import cz.filipklimes.diploma.framework.example.ui.business.User;
import cz.filipklimes.diploma.framework.example.ui.client.UserClient;
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
    private Integer signedUserId;

    public UserFacade(final UserClient userClient)
    {
        this.userClient = userClient;
        this.signedUserId = 1;
    }

    public User getUser(final Integer userId) throws UserNotFoundException
    {
        User user = userClient.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        return user;
    }

    public User getCurrentlyLoggedUser()
    {
        if (signedUserId == null) {
            return null;
        }

        try {
            return getUser(signedUserId);
        } catch (UserNotFoundException e) {
            log.error(String.format("Currently logged user with id %d cannot be found", signedUserId));
            return null;
        }
    }

    public List<User> listUsers()
    {
        return userClient.listUsers();
    }

}
