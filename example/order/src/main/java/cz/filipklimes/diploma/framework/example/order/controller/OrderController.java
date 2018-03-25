package cz.filipklimes.diploma.framework.example.order.controller;

import cz.filipklimes.diploma.framework.businessContext.Precondition;
import cz.filipklimes.diploma.framework.businessContext.exception.BusinessRulesCheckFailedException;
import cz.filipklimes.diploma.framework.example.order.business.Address;
import cz.filipklimes.diploma.framework.example.order.business.Order;
import cz.filipklimes.diploma.framework.example.order.business.User;
import cz.filipklimes.diploma.framework.example.order.client.UserClient;
import cz.filipklimes.diploma.framework.example.order.controller.response.ErrorResponse;
import cz.filipklimes.diploma.framework.example.order.facade.OrderFacade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class OrderController
{

    private final OrderFacade orderFacade;
    private final UserClient userClient;

    @Autowired
    public OrderController(final OrderFacade orderFacade, final UserClient userClient)
    {
        this.orderFacade = orderFacade;
        this.userClient = userClient;
    }

    @GetMapping("/orders")
    public ResponseEntity<?> listOrders(@RequestHeader(value = "X-User-Id", required = false) String userId)
    {
        try {
            User user = userId != null ? userClient.getUser(Integer.valueOf(userId)) : null;
            List<Order> orders = orderFacade.listOrders(user);
            return new ResponseEntity<>(orders, HttpStatus.OK);

        } catch (BusinessRulesCheckFailedException e) {
            return new ResponseEntity<>(
                new ErrorResponse(String.format(
                    "Could not list orders: %s",
                    e.getFailedRules().stream()
                        .map(Precondition::getName)
                        .collect(Collectors.joining(", "))
                )),
                HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
    }

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @RequestBody CreateOrderRequest request
    )
    {
        try {
            User user = userId != null ? userClient.getUser(Integer.valueOf(userId)) : null;
            orderFacade.createOrder(user, request.getShipping(), request.getBilling());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);

        } catch (BusinessRulesCheckFailedException e) {
            return new ResponseEntity<>(
                new ErrorResponse(String.format(
                    "Could not create order: %s",
                    e.getFailedRules().stream()
                        .map(Precondition::getName)
                        .collect(Collectors.joining(", "))
                )),
                HttpStatus.UNPROCESSABLE_ENTITY
            );
        }
    }

    private static final class CreateOrderRequest
    {

        @Getter
        @Setter
        private Address shipping;

        @Getter
        @Setter
        private Address billing;

    }

}
