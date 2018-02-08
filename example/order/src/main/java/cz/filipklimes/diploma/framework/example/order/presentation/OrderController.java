package cz.filipklimes.diploma.framework.example.order.presentation;

import cz.filipklimes.diploma.framework.example.order.business.OrderFacade;
import cz.filipklimes.diploma.framework.example.order.presentation.request.CreateOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class OrderController
{

    @Autowired
    private OrderFacade orderFacade;

    @GetMapping("/create-order")
    public String form(final Model model)
    {
        model.addAttribute("createOrderRequest", new CreateOrderRequest());
        return "create-order";
    }

    @PostMapping("/create-order")
    public String formSubmit(@ModelAttribute CreateOrderRequest createOrderRequest)
    {
        orderFacade.createOrder(createOrderRequest.getUserId(), createOrderRequest.getAddress());
        return "create-order";
    }

}
