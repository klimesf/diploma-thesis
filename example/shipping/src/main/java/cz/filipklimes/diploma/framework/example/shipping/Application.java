package cz.filipklimes.diploma.framework.example.shipping;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.Precondition;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.IsNotNull;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.logical.And;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.GrpcBusinessContextServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
public class Application
{

    private static final int BUSINESS_CONTEXT_SERVER_PORT = 5554;

    public static void main(String[] args)
    {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

        // Business Context gRPC server
        GrpcBusinessContextServer server = new GrpcBusinessContextServer(context.getBean(BusinessContextRegistry.class), BUSINESS_CONTEXT_SERVER_PORT);
        Thread t = new Thread(server);
        t.start();
    }

    @Bean
    public static BusinessContextRegistry businessContextRegistry()
    {

        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .withLocalLoader(() -> new HashSet<>(Collections.singletonList(
                BusinessContext.builder()
                    .withIdentifier(BusinessContextIdentifier.parse("shipping.correctAddress"))
                    .withPrecondition(Precondition.builder()
                        .withName("Shipping address must contain a country, city, street and postal code")
                        .withCondition(
                            new And(
                                new And(
                                    new IsNotNull<>(new ObjectPropertyReference<>("shippingAddress", "country", ExpressionType.STRING)),
                                    new IsNotNull<>(new ObjectPropertyReference<>("shippingAddress", "city", ExpressionType.STRING))
                                ),
                                new And(
                                    new IsNotNull<>(new ObjectPropertyReference<>("shippingAddress", "street", ExpressionType.STRING)),
                                    new IsNotNull<>(new ObjectPropertyReference<>("shippingAddress", "postal", ExpressionType.STRING))
                                )
                            )
                        )
                        .build())
                    .build()
            )))
            .withRemoteLoader(new RemoteBusinessContextLoader(new HashMap<>()))
            .build();

        registry.initialize();

        return registry;
    }

}
