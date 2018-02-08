package cz.filipklimes.diploma.framework.example.order.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController
{

    @RequestMapping("/")
    public String greeting(final Model model) {
        return "index";
    }

}
