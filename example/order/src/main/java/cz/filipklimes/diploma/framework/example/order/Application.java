package cz.filipklimes.diploma.framework.example.order;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleEvaluator;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleType;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.IsNotNull;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessRulesLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
public class Application
{

    private static String BILLING_HOST = "localhost";
    private static int BILLING_PORT = 5551;

    private static String SHIPPING_HOST = "localhost";
    private static int SHIPPING_PORT = 5552;

    private static String USER_HOST = "localhost";
    private static int USER_PORT = 5553;

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public static BusinessContextRegistry businessContextRegistry()
    {
        BusinessRule rule = BusinessRule.builder()
            .setName("orderItemsAreNotNull")
            .addApplicableContext("order.create")
            .addApplicableContext("order.update")
            .setType(BusinessRuleType.POST_CONDITION)
            .setCondition(new IsNotNull<>(new ObjectPropertyReference<>("order", "items", ExpressionType.VOID)))
            .build();

        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .setLocalLoader(() -> Collections.singleton(rule))
            .addRemoteLoader("billing", new RemoteBusinessRulesLoader(BILLING_HOST, BILLING_PORT)) // Billing
            .addRemoteLoader("shipping", new RemoteBusinessRulesLoader(SHIPPING_HOST, SHIPPING_PORT)) // Shipping
            .addRemoteLoader("user", new RemoteBusinessRulesLoader(USER_HOST, USER_PORT)) // User
            .build();

        registry.initialize();

        return registry;
    }

    @Bean
    public static BusinessRuleEvaluator businessRuleEvaluator(final BusinessContextRegistry businessContextRegistry)
    {
        return new BusinessRuleEvaluator(businessContextRegistry);
    }

}
