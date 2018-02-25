package cz.filipklimes.diploma.framework.example.order.aop;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleEvaluator;
import cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BusinessContextAspect
{

    private final BusinessRuleEvaluator evaluator;

    public BusinessContextAspect(final BusinessRuleEvaluator evaluator)
    {
        this.evaluator = evaluator;
    }

    @Before("@annotation(cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation)")
    public void validatePreconditions(JoinPoint joinPoint)
    {
        BusinessContext context = createBusinessContext(joinPoint);
        evaluator.evaluatePreconditions(context);
    }

    @After("@annotation(cz.filipklimes.diploma.framework.businessContext.annotation.BusinessOperation)")
    public void validatePostConditions(JoinPoint joinPoint)
    {
        BusinessContext context = createBusinessContext(joinPoint);
        evaluator.evaluatePostConditions(context);
    }

    private BusinessContext createBusinessContext(final JoinPoint joinPoint)
    {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        BusinessOperation annotation = signature.getMethod().getAnnotation(BusinessOperation.class);

        // Build business context
        BusinessContext context = new BusinessContext(annotation.value());
        String[] parameterNames = signature.getParameterNames();
        Object[] parameters = joinPoint.getArgs();
        for (int i = 0; i < parameters.length; i++) {
            context.setVariable(parameterNames[i], parameters[i]);
        }
        return context;
    }

}