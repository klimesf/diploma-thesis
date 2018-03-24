package cz.filipklimes.diploma.framework.example.ui.controller;

import cz.filipklimes.diploma.framework.example.ui.business.Product;
import cz.filipklimes.diploma.framework.example.ui.client.ProductClient;
import cz.filipklimes.diploma.framework.example.ui.client.ShoppingCartClient;
import cz.filipklimes.diploma.framework.example.ui.facade.UserFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class HomepageController
{

    private final ProductClient productClient;
    private final ShoppingCartClient shoppingCartClient;
    private final UserFacade userFacade;

    @Autowired
    public HomepageController(
        final ProductClient productClient,
        final ShoppingCartClient shoppingCartClient,
        final UserFacade userFacade
    )
    {
        this.productClient = productClient;
        this.shoppingCartClient = shoppingCartClient;
        this.userFacade = userFacade;
    }

    @GetMapping("/")
    public String homepage(Model model)
    {
        List<Product> products = productClient.listProducts();
        model.addAttribute("products", products);
        model.addAttribute("cartCount", shoppingCartClient.listCartItems().size());

        // Flash messages
        model.addAttribute("successMessage", model.asMap().get("success"));
        model.addAttribute("infoMessage", model.asMap().get("info"));
        model.addAttribute("errorMessage", model.asMap().get("error"));

        model.addAttribute("isUserLoggedIn", userFacade.getCurrentlyLoggedUser() != null);
        model.addAttribute("loggedUser", userFacade.getCurrentlyLoggedUser());

        return "index";
    }

}
