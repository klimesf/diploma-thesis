package cz.filipklimes.diploma.framework.example.billing;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyAssignment;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.VariableReference;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.Equals;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.Add;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.ProtobufBusinessContextServer;

import java.util.*;

public class Main
{

    private static final int PORT = 5551;

    public static void main(String[] args) throws InterruptedException
    {
        ProtobufBusinessContextServer server = new ProtobufBusinessContextServer(
            () -> {
                Set<BusinessRule> rules = new HashSet<>();

                // VAT percent
                BusinessRule usVat = new BusinessRule(
                    "usVat",
                    "order.create",
                    // user.country = "US"
                    new Equals<>(new ObjectPropertyReference<>("user", "country", ExpressionType.STRING), new Constant<>("US", ExpressionType.STRING)),
                    // order.vatPercent = vatPercent
                    new ObjectPropertyAssignment<>("order", "vatPercent", new Add(
                        new ObjectPropertyReference<>("order", "vatPercent", ExpressionType.NUMBER),
                        new VariableReference<>("vatPercent", ExpressionType.NUMBER)
                    ))
                );
                rules.add(usVat);

                return rules;
            },
            PORT
        );

        Thread t = new Thread(server);
        t.start();
        System.out.println("Billing rules server running");
        t.join();
    }

}
