package cz.filipklimes.diploma.framework.example.billing.controller.response;

import lombok.Getter;

public class ErrorResponse
{

    @Getter
    private final String message;

    public ErrorResponse(final String message)
    {
        this.message = message;
    }

}
