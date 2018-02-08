package cz.filipklimes.diploma.framework.example.order.presentation.request;

import lombok.Getter;
import lombok.Setter;

public class CreateOrderRequest
{

    @Getter @Setter
    public int userId;

    @Getter @Setter
    public String address;

}
