package cz.filipklimes.diploma.framework.example.order;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleEvaluator;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.FunctionCall;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;

import java.math.BigDecimal;
import java.util.*;

public class Main
{

    public static void main(String[] args)
    {
        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .addLoader(() -> {
                Set<BusinessRule> rules = new HashSet<>();

                // Send email
                BusinessRule sendEmail = new BusinessRule(
                    "sendEmail",
                    "order.create",
                    new Constant<>(true, ExpressionType.BOOL),
                    // sendEmail(user.email)
                    new FunctionCall<>(
                        "sendEmail",
                        ExpressionType.VOID,
                        new ObjectPropertyReference<>("user", "email", ExpressionType.STRING)
                    )
                );
                rules.add(sendEmail);

                return rules;
            })
            .addLoader(new RemoteBusinessContextLoader("localhost", 5551)) // Billing
            .addLoader(new RemoteBusinessContextLoader("localhost", 5552)) // Shipping
            .addLoader(new RemoteBusinessContextLoader("localhost", 5553)) // User
            .build();

        System.out.println("Loaded rules:");
        registry.getAllRules().forEach(rule -> System.out.println(" - " + rule.getName()));

        System.out.println();
        System.out.println("Creating order...");

        // Create the order
        BigDecimal vatPercent = new BigDecimal("21");
        User user = new User("John Doe", "john.doe@example.com", "US", BigDecimal.valueOf(72));
        Order order = new Order(BigDecimal.valueOf(1000), BigDecimal.ZERO, BigDecimal.ZERO);
        user.addOrder(order);

        // Build business context
        BusinessContext businessContext = new BusinessContext("order.create");
        businessContext.setVariable("order", order);
        businessContext.setVariable("user", user);
        businessContext.setVariable("vatPercent", vatPercent);
        businessContext.addFunction("sendEmail", email -> {
            System.out.printf("Sending email to: %s%n", email);
            return null;
        });

        // Evaluate (join point)
        BusinessRuleEvaluator evaluator = new BusinessRuleEvaluator(registry.getAllRules());
        evaluator.evaluate(businessContext);

        System.out.printf("Order sum: %s%n", order.getSum().toPlainString()); // Should be (1000 + 100) * 1.21
        System.out.printf("Order VAT percent: %s%n", order.getVatPercent().toPlainString()); // Should be 21
        System.out.printf("Order discount percent: %s%n", order.getDiscountPercent().toPlainString()); // Should be 20
    }

}
