package cz.filipklimes.diploma.framework.example.shipping;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.BusinessRuleType;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.IsNotNull;
import cz.filipklimes.diploma.framework.businessContext.expression.VariableReference;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.ProtobufBusinessRulesServer;

import java.util.*;

public class Main
{

    private static final int PORT = 5552;

    public static void main(String[] args) throws InterruptedException
    {
        BusinessRule addressIsNotNull = BusinessRule.builder()
            .setName("addressIsNotNull")
            .addApplicableContext("order.create")
            .setType(BusinessRuleType.PRECONDITION)
            .setCondition(new IsNotNull<>(new VariableReference<>("address", ExpressionType.STRING)))
            .build();

        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .setLocalLoader(() -> new HashSet<>(Collections.singletonList(addressIsNotNull)))
            .build();

        ProtobufBusinessRulesServer server = new ProtobufBusinessRulesServer(
            registry,
            PORT
        );

        registry.initialize();

        Thread t = new Thread(server);
        t.start();
        System.out.println("Shipping rules server running");
        t.join();
    }

}
