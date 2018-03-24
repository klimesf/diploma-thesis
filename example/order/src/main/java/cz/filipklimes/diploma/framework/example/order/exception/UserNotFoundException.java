package cz.filipklimes.diploma.framework.example.order.exception;

import lombok.Getter;

public class UserNotFoundException extends Exception
{

    @Getter
    private final Integer userId;

    public UserNotFoundException(final Integer userId)
    {
        super(String.format("User %d not found", userId));
        this.userId = userId;
    }

}
