package cz.filipklimes.diploma.framework.example.user;

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

    private static final int PORT = 5553;

    public static void main(String[] args) throws InterruptedException
    {
        BusinessRule userIsNotNull = BusinessRule.builder()
            .setName("userIsNotNull")
            .addApplicableContext("order.create")
            .setType(BusinessRuleType.PRECONDITION)
            .setCondition(new IsNotNull<>(new VariableReference<>("user", ExpressionType.VOID)))
            .build();

        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .setLocalLoader(() -> new HashSet<>(Collections.singletonList(userIsNotNull)))
            .build();

        ProtobufBusinessRulesServer server = new ProtobufBusinessRulesServer(
            registry,
            PORT
        );

        registry.initialize();

        Thread t = new Thread(server);
        t.start();
        System.out.println("User rules server running");
        t.join();
    }

}
