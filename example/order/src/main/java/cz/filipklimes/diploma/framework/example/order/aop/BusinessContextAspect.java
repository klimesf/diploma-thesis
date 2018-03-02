package cz.filipklimes.diploma.framework.example.order.aop;

import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperationParameter;
import cz.filipklimes.diploma.framework.businessContext.exception.BusinessRulesCheckFailedException;
import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessOperationContext;
import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessContextWeaver;
import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;

@Aspect
@Component
public class BusinessContextAspect
{

    private final BusinessContextWeaver evaluator;

    public BusinessContextAspect(final BusinessContextWeaver evaluator)
    {
        this.evaluator = evaluator;
    }

    @Before("@annotation(cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation)")
    public void validatePreconditions(JoinPoint joinPoint) throws BusinessRulesCheckFailedException
    {
        BusinessOperationContext context = createBusinessContext(joinPoint);
        evaluator.evaluatePreconditions(context);
    }

    @After("@annotation(cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation)")
    public void validatePostConditions(JoinPoint joinPoint)
    {
        BusinessOperationContext context = createBusinessContext(joinPoint);
        evaluator.applyPostConditions(context);
    }

    private BusinessOperationContext createBusinessContext(final JoinPoint joinPoint)
    {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        BusinessOperation annotation = signature.getMethod().getAnnotation(BusinessOperation.class);

        // Build business context
        BusinessOperationContext context = new BusinessOperationContext(annotation.value());

        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();
        Parameter[] parameters = signature.getMethod().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            BusinessOperationParameter parameterAnnotation = Arrays.stream(parameterAnnotations[i])
                .filter(a -> a instanceof BusinessOperationParameter)
                .findFirst()
                .map(BusinessOperationParameter.class::cast)
                .orElse(null);

            if (parameterAnnotation != null) {
                context.setInputParameter(parameterAnnotation.value(), parameters[i]);
            }
        }
        return context;
    }

}
