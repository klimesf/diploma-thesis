package cz.filipklimes.diploma.centralAdministration.controller;

import cz.filipklimes.diploma.centralAdministration.businessContext.BusinessContextEditor;
import cz.filipklimes.diploma.centralAdministration.businessContext.exception.CyclicDependencyException;
import cz.filipklimes.diploma.centralAdministration.businessContext.exception.MissingIncludedBusinessContextsException;
import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.xml.BusinessContextXmlLoader;
import cz.filipklimes.diploma.framework.businessContext.xml.BusinessContextXmlWriter;
import lombok.Getter;
import lombok.Setter;
import org.jdom2.JDOMException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.*;
import java.util.*;

@Controller
public class ContextController
{

    private final BusinessContextEditor editor;
    private final BusinessContextXmlLoader xmlLoader;

    public ContextController(final BusinessContextEditor editor, final BusinessContextXmlLoader xmlLoader)
    {
        this.editor = editor;
        this.xmlLoader = xmlLoader;
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

        // Flash messages
        model.addAttribute("successMessage", model.asMap().get("success"));
        model.addAttribute("infoMessage", model.asMap().get("info"));
        model.addAttribute("errorMessage", model.asMap().get("error"));

        return "detail";
    }

    @GetMapping("/edit/{identifier}")
    public String edit(@PathVariable final String identifier, final Model model) throws IOException
    {
        BusinessContext context = editor.getContext(BusinessContextIdentifier.parse(identifier));
        if (context == null) {
            return "edit";
        }

        model.addAttribute("context", context);

        BusinessContextXmlWriter writer = new BusinessContextXmlWriter();
        String contextXml = writer.writeToString(context);
        ContextForm contextForm = new ContextForm();
        contextForm.setXml(contextXml);
        model.addAttribute("contextForm", contextForm);
        model.addAttribute("contextXml", contextXml);

        // Flash messages
        model.addAttribute("successMessage", model.asMap().get("success"));
        model.addAttribute("infoMessage", model.asMap().get("info"));
        model.addAttribute("errorMessage", model.asMap().get("error"));

        return "edit";
    }

    @PostMapping("/edit/{identifier}")
    public RedirectView handleEdit(
        @PathVariable final String identifier,
        @ModelAttribute final ContextForm contextForm,
        final RedirectAttributes attributes
    ) throws IOException
    {
        try {
            BusinessContext context = xmlLoader.loadFromString(contextForm.getXml());

            if (!context.getIdentifier().toString().equals(identifier)) {
                attributes.addFlashAttribute("error", "You cannot change name of the business context");
                return new RedirectView(String.format("/edit/%s", context.getIdentifier().toString()));
            }

            editor.updateContext(context);

            attributes.addFlashAttribute("success", "Business context edited successfully");
            return new RedirectView(String.format("/detail/%s", context.getIdentifier().toString()));

        } catch (JDOMException | CyclicDependencyException | MissingIncludedBusinessContextsException e) {
            attributes.addFlashAttribute("error", e.getMessage());
            return new RedirectView(String.format("/edit/%s", identifier));
        }
    }

    private static final class ContextForm
    {

        @Getter
        @Setter
        private String xml;

    }

}
