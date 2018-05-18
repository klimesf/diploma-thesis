package cz.filipklimes.diploma.framework.example.ui.controller;

import cz.filipklimes.diploma.framework.example.ui.business.Product;
import cz.filipklimes.diploma.framework.example.ui.client.OrderClient;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotChangePriceException;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotChangeStockException;
import cz.filipklimes.diploma.framework.example.ui.facade.ProductFacade;
import cz.filipklimes.diploma.framework.example.ui.facade.SignedUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;

@Controller
public class ProductController
{

    private final OrderClient orderClient;
    private final SignedUser signedUser;
    private final ProductFacade productFacade;

    @Autowired
    public ProductController(
        final OrderClient orderClient,
        final SignedUser signedUser,
        final ProductFacade productFacade
    )
    {
        this.orderClient = orderClient;
        this.signedUser = signedUser;
        this.productFacade = productFacade;
    }

    @GetMapping("/change-price/{productId}")
    public String changePrice(@PathVariable Integer productId, Model model)
    {
        // Header info
        model.addAttribute("cartCount", orderClient.listCartItems().size());
        model.addAttribute("isUserLoggedIn", signedUser.isAnyoneSignedIn());
        model.addAttribute("user", signedUser.getCurrentlyLoggedUser());
        model.addAttribute("changePriceForm", new ChangePriceForm());
        model.addAttribute("productId", productId);

        return "change-price";
    }

    @PostMapping("/change-price/{productId}")
    public RedirectView handleChangePrice(@PathVariable Integer productId, @ModelAttribute ChangePriceForm changePriceForm, RedirectAttributes attributes)
    {
        try {
            Product product = productFacade.changePrice(
                productId,
                Optional.of(changePriceForm.getCostPrice()).filter(s -> !s.isEmpty()).orElse(null),
                Optional.of(changePriceForm.getSellPrice()).filter(s -> !s.isEmpty()).orElse(null)
            );
            attributes.addFlashAttribute("success", String.format("Changed price for product %s", product.getName()));

        } catch (CouldNotChangePriceException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }
        return new RedirectView("/");
    }

    @GetMapping("/change-stock/{productId}")
    public String changeStock(@PathVariable Integer productId, Model model)
    {
        // Header info
        model.addAttribute("cartCount", orderClient.listCartItems().size());
        model.addAttribute("isUserLoggedIn", signedUser.isAnyoneSignedIn());
        model.addAttribute("user", signedUser.getCurrentlyLoggedUser());
        model.addAttribute("changeStockForm", new ChangeStockForm());
        model.addAttribute("productId", productId);

        return "change-stock";
    }

    @PostMapping("/change-stock/{productId}")
    public RedirectView handleChangeStock(@PathVariable Integer productId, @ModelAttribute ChangeStockForm changeStockForm, RedirectAttributes attributes)
    {
        try {
            Product product = productFacade.changeStock(
                productId,
                Optional.of(changeStockForm.getStockCount()).filter(s -> !s.isEmpty()).orElse(null)
            );
            attributes.addFlashAttribute("success", String.format("Changed stock for product %s", product.getName()));

        } catch (CouldNotChangeStockException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }
        return new RedirectView("/");
    }


    public static class ChangePriceForm
    {

        @Getter
        @Setter
        private String costPrice;

        @Getter
        @Setter
        private String sellPrice;

    }


    public static class ChangeStockForm
    {

        @Getter
        @Setter
        private String stockCount;

    }

}
