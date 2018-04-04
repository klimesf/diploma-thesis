package cz.filipklimes.diploma.centralAdministration.controller;

import cz.filipklimes.diploma.centralAdministration.businessContext.BusinessContextEditor;
import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Controller
public class ContextController
{

    final BusinessContextEditor editor;

    public ContextController(final BusinessContextEditor editor)
    {
        this.editor = editor;
    }

    @GetMapping("/")
    public String homepage(final Model model)
    {
        Map<String, List<BusinessContext>> contextPerHost = new HashMap<>();
        editor.getContexts().values()
            .forEach(context -> {
                String prefix = context.getIdentifier().getPrefix();
                contextPerHost.computeIfAbsent(prefix, p -> new ArrayList<>()).add(context);
            });

        model.addAttribute("contexts", contextPerHost);

        return "index";
    }

    @GetMapping("/detail/{identifier}")
    public String detail(@PathVariable final String identifier, final Model model)
    {
        BusinessContext context = editor.getContext(BusinessContextIdentifier.parse(identifier));
        model.addAttribute("context", context);

        return "detail";
    }

}
