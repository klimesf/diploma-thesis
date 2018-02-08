package cz.filipklimes.diploma.framework.example.order.presentation;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleType;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class BusinessRulesController
{

    @Autowired
    private BusinessContextRegistry registry;

    @GetMapping("/rules")
    public String greeting(final Model model)
    {
        Set<BusinessRuleEntry> businessRuleEntries = new HashSet<>();
        for (Map.Entry<String, Set<BusinessRule>> entry : registry.getAllRules().entrySet()) {
            for (BusinessRule businessRule : entry.getValue()) {
                businessRuleEntries.add(new BusinessRuleEntry(entry.getKey(), businessRule));
            }
        }

        model.addAttribute("rules", businessRuleEntries);
        return "rules";
    }

    public static final class BusinessRuleEntry
    {

        @Getter
        private final String origin;

        @Getter
        private final String businessRuleName;

        @Getter
        private final String businessRuleType;

        @Getter
        private final String applicableContexts;

        @Getter
        private final String condition;

        private BusinessRuleEntry(
            final String origin,
            final BusinessRule businessRule
        )
        {
            this.origin = origin;
            this.businessRuleName = businessRule.getName();
            this.businessRuleType = ruleTypeToString(businessRule.getType());
            this.applicableContexts = businessRule.getApplicableContexts().stream().collect(Collectors.joining(", "));
            this.condition = businessRule.getCondition().toString();
        }

        private String ruleTypeToString(final BusinessRuleType type)
        {
            switch (type) {
                case PRECONDITION:
                    return "precondition";
                case POST_CONDITION:
                    return "post-condition";
                default:
                    return "unknown";
            }
        }

    }

}
