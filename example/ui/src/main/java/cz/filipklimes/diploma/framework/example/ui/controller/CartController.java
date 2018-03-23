package cz.filipklimes.diploma.framework.example.ui.controller;

import cz.filipklimes.diploma.framework.example.ui.business.Product;
import cz.filipklimes.diploma.framework.example.ui.exception.ProductNotFoundException;
import cz.filipklimes.diploma.framework.example.ui.facade.CartFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class CartController
{

    private final CartFacade cartFacade;

    @Autowired
    public CartController(final CartFacade cartFacade)
    {
        this.cartFacade = cartFacade;
    }

    @GetMapping("/add-to-cart/{productId}")
    public RedirectView addToCart(@PathVariable Integer productId, RedirectAttributes attributes)
    {
        try {
            Product product = cartFacade.addProduct(productId);
            attributes.addFlashAttribute("success", String.format("Product %s added to shopping cart", product.getName()));
        } catch (ProductNotFoundException e) {
            attributes.addFlashAttribute("error", "Product could not be added to shopping cart, it does not exist");
        }
        return new RedirectView("/");
    }

}
