package cz.filipklimes.diploma.framework.businessContext.weaver;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.PostCondition;
import cz.filipklimes.diploma.framework.businessContext.Precondition;
import cz.filipklimes.diploma.framework.businessContext.exception.BusinessRulesCheckFailedException;
import lombok.Getter;

import java.lang.reflect.Field;
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

    public void applyPostConditions(final BusinessOperationContext operationContext)
    {
        BusinessContextIdentifier businessContextidentifier = BusinessContextIdentifier.parse(operationContext.getName());
        BusinessContext businessContext = registry.getContextByIdentifier(businessContextidentifier);

        businessContext.getPostConditions().forEach(postCondition -> {
            switch (postCondition.getType()) {
                case FILTER_OBJECT_FIELD:
                    filterObjectField(operationContext, postCondition);
                    break;
                case FILTER_LIST_OF_OBJECTS:
                case FILTER_LIST_OF_OBJECTS_FIELD:
                    // TODO: implement
                    throw new UnsupportedOperationException("not implemented yet");
                default:
                    throw new RuntimeException("Unsupported PostConditionType");
            }
        });
    }

    private void filterObjectField(final BusinessOperationContext operationContext, final PostCondition postCondition)
    {
        if (!postCondition.getCondition().interpret(operationContext)) {
            return;
        }


        try {
            Object object = operationContext.getOutput();
            Field declaredField = object.getClass().getDeclaredField(postCondition.getReferenceName());
            declaredField.setAccessible(true);
            declaredField.set(object, null);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Could not access object property", e);
        }
    }

    private static final class EvaluationResult
    {

        private final boolean passed;

        @Getter
        private final Precondition rule;

        private EvaluationResult(final boolean passed, final Precondition rule)
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
