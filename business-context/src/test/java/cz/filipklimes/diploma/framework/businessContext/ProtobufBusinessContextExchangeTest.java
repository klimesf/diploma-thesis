package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.GreaterThan;
import cz.filipklimes.diploma.framework.businessContext.loader.client.protobuf.ProtobufBusinessContextClient;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.ProtobufBusinessRulesServer;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

public class ProtobufBusinessContextExchangeTest
{

    @Test
    public void test()
    {
        BusinessContextRegistry.Builder builder = BusinessContextRegistry.builder();
        builder.setLocalLoader(() -> {
            Set<BusinessRule> rules = new HashSet<>();

            BusinessRule orderSumIsPositive = new BusinessRule(
                "orderSumIsPositive",
                Collections.singleton("order.create"),
                BusinessRuleType.PRECONDITION,
                new GreaterThan(
                    new ObjectPropertyReference<>("order", "sum", ExpressionType.NUMBER),
                    new Constant<>(BigDecimal.ZERO, ExpressionType.NUMBER)
                )
            );
            rules.add(orderSumIsPositive);

            return rules;
        });

        BusinessContextRegistry registry = builder.build();
        registry.initialize();

        ProtobufBusinessRulesServer server = new ProtobufBusinessRulesServer(registry, 5555);
        Thread t = new Thread(server);
        t.start();

        ProtobufBusinessContextClient client = new ProtobufBusinessContextClient("localhost", 5555);
        Set<BusinessRule> rules = client.receiveRules();

        t.interrupt();
        Assert.assertEquals(1, rules.size());

        BusinessRule rule = rules.iterator().next();
        Assert.assertTrue(rule.getCondition() instanceof GreaterThan);
        GreaterThan condition = (GreaterThan) rule.getCondition();
        Assert.assertTrue(condition.getLeft() instanceof ObjectPropertyReference);
        Assert.assertTrue(condition.getRight() instanceof Constant);
    }

}
