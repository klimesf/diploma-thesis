package cz.filipklimes.diploma.framework.example.ui.controller;

import cz.filipklimes.diploma.framework.example.ui.business.Address;
import cz.filipklimes.diploma.framework.example.ui.client.OrderClient;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotCreateOrderException;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotListOrdersException;
import cz.filipklimes.diploma.framework.example.ui.facade.SignedUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public class OrderController
{

    private final OrderClient orderClient;
    private final SignedUser signedUser;

    @Autowired
    public OrderController(final OrderClient orderClient, final SignedUser signedUser)
    {
        this.orderClient = orderClient;
        this.signedUser = signedUser;
    }

    @GetMapping("/orders")
    public String listOrders(Model model, RedirectAttributes attributes)
    {
        try {
            model.addAttribute("orders", orderClient.listOrders());

            // Header info
            model.addAttribute("cartCount", orderClient.listCartItems().size());
            model.addAttribute("isUserLoggedIn", signedUser.isAnyoneSignedIn());
            model.addAttribute("user", signedUser.getCurrentlyLoggedUser());
        } catch (CouldNotListOrdersException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }

        return "orders";
    }

    @GetMapping("/checkout")
    public String checkout(Model model)
    {
        model.addAttribute("checkoutForm", new CheckoutForm());

        // Header info
        model.addAttribute("cartCount", orderClient.listCartItems().size());
        model.addAttribute("isUserLoggedIn", signedUser.isAnyoneSignedIn());
        model.addAttribute("user", signedUser.getCurrentlyLoggedUser());

        return "checkout";
    }

    @PostMapping("/checkout")
    public RedirectView handleCheckout(@ModelAttribute CheckoutForm checkoutForm, RedirectAttributes attributes)
    {
        try {
            orderClient.createOrder(
                checkoutForm.createShippingAddress(),
                checkoutForm.createBillingAddress()
            );
            attributes.addFlashAttribute("success", "Your order has been created!");

        } catch (CouldNotCreateOrderException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }

        return new RedirectView("/");
    }

    private static final class CheckoutForm
    {

        @Getter
        @Setter
        private String shippingCountry;

        @Getter
        @Setter
        private String shippingCity;

        @Getter
        @Setter
        private String shippingStreet;

        @Getter
        @Setter
        private String shippingPostal;

        @Getter
        @Setter
        private String billingCountry;

        @Getter
        @Setter
        private String billingCity;

        @Getter
        @Setter
        private String billingStreet;

        @Getter
        @Setter
        private String billingPostal;

        private Address createShippingAddress()
        {
            return new Address(
                Optional.of(getShippingCountry()).filter(s -> !s.isEmpty()).orElse(null),
                Optional.of(getShippingCity()).filter(s -> !s.isEmpty()).orElse(null),
                Optional.of(getShippingStreet()).filter(s -> !s.isEmpty()).orElse(null),
                Optional.of(getShippingPostal()).filter(s -> !s.isEmpty()).orElse(null)
            );
        }

        private Address createBillingAddress()
        {
            return new Address(
                Optional.of(getBillingCountry()).filter(s -> !s.isEmpty()).orElse(null),
                Optional.of(getBillingCity()).filter(s -> !s.isEmpty()).orElse(null),
                Optional.of(getBillingStreet()).filter(s -> !s.isEmpty()).orElse(null),
                Optional.of(getBillingPostal()).filter(s -> !s.isEmpty()).orElse(null)
            );
        }

    }

}
