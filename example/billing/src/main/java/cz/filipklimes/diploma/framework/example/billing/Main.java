package cz.filipklimes.diploma.framework.example.billing;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleType;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.GreaterThan;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.ProtobufBusinessRulesServer;

import java.math.BigDecimal;
import java.util.*;

public class Main
{

    private static final int PORT = 5551;

    public static void main(String[] args) throws InterruptedException
    {
        BusinessRule orderHasPositiveSum = BusinessRule.builder()
            .setName("orderHasPositiveSum")
            .addApplicableContext("order.create")
            .addApplicableContext("order.update")
            .setType(BusinessRuleType.POST_CONDITION)
            .setCondition(new GreaterThan(
                new ObjectPropertyReference<>("order", "sum", ExpressionType.NUMBER),
                new Constant<>(BigDecimal.ZERO, ExpressionType.NUMBER)
            ))
            .build();

        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .setLocalLoader(() -> new HashSet<>(Collections.singletonList(orderHasPositiveSum)))
            .build();

        registry.initialize();

        ProtobufBusinessRulesServer server = new ProtobufBusinessRulesServer(
            registry,
            PORT
        );

        Thread t = new Thread(server);
        t.start();
        System.out.println("Billing rules server running");
        t.join();
    }

}
