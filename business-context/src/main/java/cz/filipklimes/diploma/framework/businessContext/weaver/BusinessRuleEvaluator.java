package cz.filipklimes.diploma.framework.businessContext.weaver;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.exception.BusinessRulesCheckFailedException;
import lombok.Getter;

public class BusinessRuleEvaluator
{

    private final BusinessContextRegistry registry;

    public BusinessRuleEvaluator(final BusinessContextRegistry registry)
    {
        this.registry = registry;
    }

    /**
     * Evaluates preconditions applicable to the business context and throws BusinessRulesCheckFailedException
     * if any rule was not satisfied.
     *
     * @param context The business context.
     * @throws BusinessRulesCheckFailedException When any Business Rule was not satisfied.
     */
    public void evaluatePreconditions(final BusinessOperationContext context) throws BusinessRulesCheckFailedException
    {
//        Set<EvaluationResult> failedRules = registry.findPreconditions(context.getName()).stream()
//            .map(rule -> new EvaluationResult(rule.getCondition().interpret(context), rule))
//            .filter(EvaluationResult::hasNotPassed)
//            .collect(Collectors.toSet());
//
//        if (!failedRules.isEmpty()) {
//            throw new BusinessRulesCheckFailedException(failedRules.stream().map(EvaluationResult::getRule).collect(Collectors.toSet()));
//        }
    }

    /**
     * Evaluates postconditions applicable to the business context and throws BusinessRulesCheckFailedException
     * if any rule was not satisfied.
     *
     * @param context The business context.
     * @throws BusinessRulesCheckFailedException When any Business Rule was not satisfied.
     */
    public void evaluatePostConditions(final BusinessOperationContext context) throws BusinessRulesCheckFailedException
    {
//        Set<EvaluationResult> failedRules = registry.findPostConditions(context.getName()).stream()
//            .map(rule -> new EvaluationResult(rule.getCondition().interpret(context), rule))
//            .filter(EvaluationResult::hasNotPassed)
//            .collect(Collectors.toSet());
//
//        if (!failedRules.isEmpty()) {
//            throw new BusinessRulesCheckFailedException(failedRules.stream().map(EvaluationResult::getRule).collect(Collectors.toSet()));
//        }
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
