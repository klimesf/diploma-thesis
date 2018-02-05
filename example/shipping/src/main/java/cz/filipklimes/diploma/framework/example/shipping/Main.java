package cz.filipklimes.diploma.framework.example.shipping;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyAssignment;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.Equals;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Add;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.ProtobufBusinessContextServer;

import java.math.BigDecimal;
import java.util.*;

public class Main
{

    private static final int PORT = 5552;

    public static void main(String[] args) throws InterruptedException
    {
        ProtobufBusinessContextServer server = new ProtobufBusinessContextServer(
            () -> {
                Set<BusinessRule> rules = new HashSet<>();

                // Shipping to countries
                BusinessRule usShipping = new BusinessRule(
                    "usShipping",
                    "order.create",
                    // user.country = "US"
                    new Equals<>(new ObjectPropertyReference<>("user", "country", ExpressionType.STRING), new Constant<>("US", ExpressionType.STRING)),
                    // order.sum = order.sum + 100
                    new ObjectPropertyAssignment<>("order", "sum", new Add(
                        new ObjectPropertyReference<>("order", "sum", ExpressionType.NUMBER),
                        new Constant<>(BigDecimal.valueOf(100), ExpressionType.NUMBER)
                    ))
                );
                rules.add(usShipping);

                BusinessRule gbShipping = new BusinessRule(
                    "gbShipping",
                    "order.create",
                    // user.country = "GB"
                    new Equals<>(new ObjectPropertyReference<>("user", "country", ExpressionType.STRING), new Constant<>("GB", ExpressionType.STRING)),
                    // order.sum = order.sum + 200
                    new ObjectPropertyAssignment<>("order", "sum", new Add(
                        new ObjectPropertyReference<>("order", "sum", ExpressionType.NUMBER),
                        new Constant<>(BigDecimal.valueOf(200), ExpressionType.NUMBER)
                    ))
                );
                rules.add(gbShipping);

                return rules;
            },
            PORT
        );

        Thread t = new Thread(server);
        t.start();
        System.out.println("Shipping rules server running");
        t.join();
    }

}
