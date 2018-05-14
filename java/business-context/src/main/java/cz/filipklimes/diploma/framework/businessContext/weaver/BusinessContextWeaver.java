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
    public void evaluatePreconditions(final BusinessOperationContext operationContext)
    {
        BusinessContextIdentifier businessContextidentifier = BusinessContextIdentifier.parse(operationContext.getName());
        registry.waitForTransaction();
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
        if (operationContext.getOutput() == null) {
            return;
        }

        BusinessContextIdentifier businessContextidentifier = BusinessContextIdentifier.parse(operationContext.getName());
        registry.waitForTransaction();
        BusinessContext businessContext = registry.getContextByIdentifier(businessContextidentifier);

        businessContext.getPostConditions().forEach(postCondition -> {
            switch (postCondition.getType()) {
                case FILTER_OBJECT_FIELD:
                    filterObjectField(operationContext, postCondition);
                    break;
                case FILTER_LIST_OF_OBJECTS:
                    filterListOfObjects(operationContext, postCondition);
                    break;
                case FILTER_LIST_OF_OBJECTS_FIELD:
                    filterListOfObjectsField(operationContext, postCondition);
                    break;
                default:
                    throw new RuntimeException("Unsupported PostConditionType");
            }
        });
    }

    private void filterObjectField(final BusinessOperationContext operationContext, final PostCondition postCondition)
    {
        Object object = operationContext.getOutput();

        if (!postCondition.getCondition().interpret(operationContext)) {
            return;
        }

        try {
            Field declaredField = object.getClass().getDeclaredField(postCondition.getReferenceName());
            declaredField.setAccessible(true);
            declaredField.set(object, null);

        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Could not access object property", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void filterListOfObjects(final BusinessOperationContext operationContext, final PostCondition postCondition)
    {
        Iterable list = (Iterable<?>) operationContext.getOutput();

        Collection result = new ArrayList<>();
        list.forEach(item -> {
            try {
                BusinessOperationContext itemContext = operationContext.clone();
                itemContext.setInputParameter("item", item);

                if (!postCondition.getCondition().interpret(itemContext)) {
                    return;
                }

                result.add(item);

            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Could not access object property", e);
            }
        });

        operationContext.setOutput(Collections.unmodifiableCollection(result));
    }

    private void filterListOfObjectsField(final BusinessOperationContext operationContext, final PostCondition postCondition)
    {
        Iterable<?> list = (Iterable<?>) operationContext.getOutput();
        list.forEach(item -> {
            try {
                BusinessOperationContext itemContext = operationContext.clone();
                itemContext.setInputParameter("item", item);

                if (!postCondition.getCondition().interpret(itemContext)) {
                    return;
                }

                Field declaredField = item.getClass().getDeclaredField(postCondition.getReferenceName());
                declaredField.setAccessible(true);
                declaredField.set(item, null);

            } catch (IllegalAccessException | NoSuchFieldException | CloneNotSupportedException e) {
                throw new RuntimeException("Could not access object property", e);
            }
        });
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
