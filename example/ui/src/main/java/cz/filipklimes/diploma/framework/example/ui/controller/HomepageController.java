package cz.filipklimes.diploma.framework.example.ui.controller;

import cz.filipklimes.diploma.framework.example.ui.business.Product;
import cz.filipklimes.diploma.framework.example.ui.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;

@Controller
public class HomepageController
{

    private final ProductRepository productRepository;

    @Autowired
    public HomepageController(final ProductRepository productRepository)
    {
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String homepage(Model model)
    {
        List<Product> products = productRepository.listProducts();
        model.addAttribute("products", products);

        // Flash messages
        model.addAttribute("successMessage", model.asMap().get("success"));
        model.addAttribute("infoMessage", model.asMap().get("info"));
        model.addAttribute("errorMessage", model.asMap().get("error"));

        return "index";
    }

}
