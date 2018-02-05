package cz.filipklimes.diploma.framework.businessContext;

import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyAssignment;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.GreaterOrEqualTo;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Multiply;
import cz.filipklimes.diploma.framework.businessContext.loader.client.protobuf.ProtobufBusinessContextClient;
import cz.filipklimes.diploma.framework.businessContext.provider.BusinessContextProvider;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.ProtobufBusinessContextServer;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

public class ProtobufBusinessContextExchangeTest
{

    @Test
    public void test()
    {
        BusinessContextProvider provider = () -> {
            Set<BusinessRule> rules = new HashSet<>();

            BusinessRule discountForElders = new BusinessRule(
                "discount for elders",
                "order.create",
                // if user.age >= 70
                new GreaterOrEqualTo(new ObjectPropertyReference<>("user", "age", ExpressionType.NUMBER), new Constant<>(new BigDecimal(70), ExpressionType.NUMBER)),
                // then order.sum = order.sum * 80 %
                new ObjectPropertyAssignment<>("order", "sum", new Multiply(
                    new ObjectPropertyReference<>("order", "sum", ExpressionType.NUMBER),
                    new Constant<>(new BigDecimal("0.8"), ExpressionType.NUMBER)
                ))
            );
            rules.add(discountForElders);

            return rules;
        };

        ProtobufBusinessContextServer server = new ProtobufBusinessContextServer(provider, 5555);
        Thread t = new Thread(server);
        t.start();

        ProtobufBusinessContextClient client = new ProtobufBusinessContextClient("localhost", 5555);
        Set<BusinessRule> rules = client.receiveRules();

        t.interrupt();
        Assert.assertEquals(1, rules.size());

        BusinessRule rule = rules.iterator().next();
        Assert.assertTrue(rule.getLeftHandSide() instanceof GreaterOrEqualTo);
        Assert.assertTrue(((GreaterOrEqualTo) rule.getLeftHandSide()).getLeft() instanceof ObjectPropertyReference);
        Assert.assertTrue(((GreaterOrEqualTo) rule.getLeftHandSide()).getRight() instanceof Constant);
        Assert.assertTrue(rule.getRightHandSide() instanceof ObjectPropertyAssignment);
        Assert.assertTrue(((ObjectPropertyAssignment) rule.getRightHandSide()).getArgument() instanceof Multiply);
    }

}
