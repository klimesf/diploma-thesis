package cz.filipklimes.diploma.framework.businessContext.weaver;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.exception.BusinessRulesCheckFailedException;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class BusinessContextWeaver
{

    private final BusinessContextRegistry registry;

    public BusinessContextWeaver(final BusinessContextRegistry registry)
    {
        this.registry = registry;
    }

    /**
     * Evaluates preconditions applicable to the business operation context and throws BusinessRulesCheckFailedException
     * if any rule was not satisfied.
     *
     * @param operationContext The business operation context.
     * @throws BusinessRulesCheckFailedException When any Business Rule was not satisfied.
     */
    public void evaluatePreconditions(final BusinessOperationContext operationContext) throws BusinessRulesCheckFailedException
    {
        BusinessContextIdentifier businessContextidentifier = BusinessContextIdentifier.parse(operationContext.getName());
        BusinessContext businessContext = registry.getContextByIdentifier(businessContextidentifier);

        Set<EvaluationResult> failedRules = businessContext.getPreconditions().stream()
            .map(rule -> new EvaluationResult(rule.getCondition().interpret(operationContext), rule))
            .filter(EvaluationResult::hasNotPassed)
            .collect(Collectors.toSet());

        if (!failedRules.isEmpty()) {
            throw new BusinessRulesCheckFailedException(failedRules.stream().map(EvaluationResult::getRule).collect(Collectors.toSet()));
        }
    }

    public void applyPostConditions(final BusinessOperationContext context)
    {
    }

    private static final class EvaluationResult
    {

        private final boolean passed;

        @Getter
        private final BusinessRule rule;

        private EvaluationResult(final boolean passed, final BusinessRule rule)
        {
            this.passed = passed;
            this.rule = rule;
        }

        private boolean hasNotPassed()
        {
            return !passed;
        }

    }

}
