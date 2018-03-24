package cz.filipklimes.diploma.framework.example.ui.controller;

import cz.filipklimes.diploma.framework.example.ui.business.Product;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotAddShoppingCartItemException;
import cz.filipklimes.diploma.framework.example.ui.exception.ProductNotFoundException;
import cz.filipklimes.diploma.framework.example.ui.facade.ShoppingCartFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ShoppingCartController
{

    private final ShoppingCartFacade shoppingCartFacade;

    @Autowired
    public ShoppingCartController(final ShoppingCartFacade shoppingCartFacade)
    {
        this.shoppingCartFacade = shoppingCartFacade;
    }

    @GetMapping("/add-to-cart/{productId}")
    public RedirectView addToCart(@PathVariable Integer productId, RedirectAttributes attributes)
    {
        try {
            Product product = shoppingCartFacade.addProduct(productId);
            attributes.addFlashAttribute("success", String.format("Product %s added to shopping cart", product.getName()));
        } catch (ProductNotFoundException e) {
            attributes.addFlashAttribute("error", "Product could not be added to shopping cart, it does not exist");
        } catch (CouldNotAddShoppingCartItemException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }
        return new RedirectView("/");
    }

}
