package cz.filipklimes.diploma.framework.businessContext;

import java.util.*;

public class BusinessRuleEvaluator
{

    private final Set<BusinessRule> ruleSet;

    public BusinessRuleEvaluator(final Set<BusinessRule> ruleSet)
    {
        this.ruleSet = Collections.unmodifiableSet(ruleSet);
    }

    public void evaluate(final BusinessContext context)
    {
        ruleSet.stream()
            // Filter the expressions not applicable within the context
            .filter(rule -> rule.getBusinessContextName().equals(context.getName()))
            // Filter the expressions where left hand side criteria is not met
            .filter(rule -> rule.getLeftHandSide().interpret(context))
            // Apply right hand side expressions
            .forEach(rule -> rule.getRightHandSide().interpret(context));
    }

}
