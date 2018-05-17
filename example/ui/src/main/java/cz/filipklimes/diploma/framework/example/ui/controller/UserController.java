package cz.filipklimes.diploma.framework.example.ui.controller;

import cz.filipklimes.diploma.framework.example.ui.business.User;
import cz.filipklimes.diploma.framework.example.ui.client.OrderClient;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotCreateUserException;
import cz.filipklimes.diploma.framework.example.ui.exception.UserNotFoundException;
import cz.filipklimes.diploma.framework.example.ui.facade.SignedUser;
import cz.filipklimes.diploma.framework.example.ui.facade.UserFacade;
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
public class UserController
{

    private final OrderClient orderClient;
    private final UserFacade userFacade;
    private final SignedUser signedUser;

    @Autowired
    public UserController(
        final OrderClient orderClient,
        final UserFacade userFacade,
        final SignedUser signedUser
    )
    {
        this.orderClient = orderClient;
        this.userFacade = userFacade;
        this.signedUser = signedUser;
    }

    @GetMapping("/users")
    public String listUsers(Model model)
    {
        model.addAttribute("users", userFacade.listUsers());

        // Header info
        model.addAttribute("cartCount", orderClient.listCartItems().size());
        model.addAttribute("isUserLoggedIn", signedUser.isAnyoneSignedIn());
        model.addAttribute("user", signedUser.getCurrentlyLoggedUser());

        return "users";
    }

    @GetMapping("/sign-in/{userId}")
    public RedirectView signIn(@PathVariable Integer userId, RedirectAttributes attributes)
    {
        try {
            User user = userFacade.getUser(userId);
            signedUser.signIn(user);
            attributes.addFlashAttribute("success", String.format("Signed in as %s", user.getName()));
        } catch (UserNotFoundException e) {
            attributes.addFlashAttribute("error", "Cannot sign in, user does not exist");
        }
        return new RedirectView("/");
    }

    @GetMapping("/register")
    public String register(Model model)
    {
        // Header info
        model.addAttribute("cartCount", orderClient.listCartItems().size());
        model.addAttribute("isUserLoggedIn", signedUser.isAnyoneSignedIn());
        model.addAttribute("user", signedUser.getCurrentlyLoggedUser());
        model.addAttribute("userForm", new UserForm());

        return "register";
    }

    @PostMapping("/register")
    public RedirectView handleRegister(@ModelAttribute UserForm userForm, RedirectAttributes attributes)
    {
        try {
            User user = userFacade.register(
                Optional.of(userForm.getName()).filter(s -> !s.isEmpty()).orElse(null),
                Optional.of(userForm.getEmail()).filter(s -> !s.isEmpty()).orElse(null)
            );
            signedUser.signIn(user);
            attributes.addFlashAttribute("success", String.format("Registered new user %s", user.getName()));

        } catch (CouldNotCreateUserException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }
        return new RedirectView("/");
    }

    @GetMapping("/create-employee")
    public String createEmployee(Model model)
    {
        // Header info
        model.addAttribute("cartCount", orderClient.listCartItems().size());
        model.addAttribute("isUserLoggedIn", signedUser.isAnyoneSignedIn());
        model.addAttribute("user", signedUser.getCurrentlyLoggedUser());
        model.addAttribute("employeeForm", new UserForm());

        return "create-employee";
    }

    @PostMapping("/create-employee")
    public RedirectView handleCreateEmployee(@ModelAttribute UserForm employeeForm, RedirectAttributes attributes)
    {
        try {
            User user = userFacade.createEmployee(
                Optional.of(employeeForm.getName()).filter(s -> !s.isEmpty()).orElse(null),
                Optional.of(employeeForm.getEmail()).filter(s -> !s.isEmpty()).orElse(null)
            );
            attributes.addFlashAttribute("success", String.format("Registered new employee %s", user.getName()));

        } catch (CouldNotCreateUserException e) {
            attributes.addFlashAttribute("error", e.getMessage());
        }
        return new RedirectView("/");
    }

    private static final class UserForm
    {

        @Getter
        @Setter
        private String name;

        @Getter
        @Setter
        private String email;

    }

}
