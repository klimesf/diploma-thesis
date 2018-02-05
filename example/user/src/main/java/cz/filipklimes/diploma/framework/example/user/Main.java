package cz.filipklimes.diploma.framework.example.user;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyAssignment;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Add;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.GreaterOrEqualTo;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.ProtobufBusinessContextServer;

import java.math.BigDecimal;
import java.util.*;

public class Main
{

    private static final int PORT = 5553;

    public static void main(String[] args) throws InterruptedException
    {
        ProtobufBusinessContextServer server = new ProtobufBusinessContextServer(
            () -> {
                Set<BusinessRule> rules = new HashSet<>();

                // Discounts
                BusinessRule elderDiscount = new BusinessRule(
                    "elderDiscount",
                    "order.create",
                    // user.age >= 70
                    new GreaterOrEqualTo(new ObjectPropertyReference<>("user", "age", ExpressionType.NUMBER), new Constant<>(BigDecimal.valueOf(70), ExpressionType.NUMBER)),
                    // order.discountPercent = order.discountPercent + 20
                    new ObjectPropertyAssignment<>("order", "discountPercent", new Add(
                        new ObjectPropertyReference<>("order", "discountPercent", ExpressionType.NUMBER),
                        new Constant<>(BigDecimal.valueOf(200), ExpressionType.NUMBER)
                    ))
                );
                rules.add(elderDiscount);

                return rules;
            },
            PORT
        );

        Thread t = new Thread(server);
        t.start();
        System.out.println("User rules server running");
        t.join();
    }

}
