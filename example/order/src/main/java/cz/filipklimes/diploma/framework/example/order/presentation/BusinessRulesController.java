package cz.filipklimes.diploma.framework.example.order.presentation;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BusinessRulesController
{

    @Autowired
    private BusinessContextRegistry registry;

    @GetMapping("/rules")
    public String greeting(final Model model) {
        model.addAttribute("rules", registry.getAllRules());
        return "rules";
    }

}
