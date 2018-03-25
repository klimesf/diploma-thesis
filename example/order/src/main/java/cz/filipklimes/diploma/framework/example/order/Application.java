package cz.filipklimes.diploma.framework.example.order;

import cz.filipklimes.diploma.framework.businessContext.BusinessContext;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextIdentifier;
import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.Precondition;
import cz.filipklimes.diploma.framework.businessContext.aop.BusinessContextAspect;
import cz.filipklimes.diploma.framework.businessContext.expression.Constant;
import cz.filipklimes.diploma.framework.businessContext.expression.ExpressionType;
import cz.filipklimes.diploma.framework.businessContext.expression.ObjectPropertyReference;
import cz.filipklimes.diploma.framework.businessContext.expression.numeric.LessThan;
import cz.filipklimes.diploma.framework.businessContext.loader.LocalBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteServiceAddress;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.grpc.GrpcRemoteLoader;
import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessContextWeaver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.*;

@SpringBootApplication
public class Application
{

    private static String AUTH_SERVICE_HOST = "localhost";
    private static int AUTH_SERVICE_PORT = 5553;

    private static String PRODUCT_SERVICE_HOST = "localhost";
    private static int PRODUCT_SERVICE_PORT = 5552;

    private static String USER_SERVICE_HOST = "localhost";
    private static int USER_SERVICE_PORT = 5553;

    private static String SHIPPING_SERVICE_HOST = "localhost";
    private static int SHIPPING_SERVICE_PORT = 5554;

    private static String BILLING_SERVICE_HOST = "localhost";
    private static int BILLING_SERVICE_PORT = 5555;

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public static BusinessContextRegistry businessContextRegistry()
    {
        Map<String, RemoteLoader> remoteLoaders = new HashMap<>();
        remoteLoaders.put("product", new GrpcRemoteLoader(new RemoteServiceAddress("product", PRODUCT_SERVICE_HOST, PRODUCT_SERVICE_PORT)));
        remoteLoaders.put("user", new GrpcRemoteLoader(new RemoteServiceAddress("user", USER_SERVICE_HOST, USER_SERVICE_PORT)));
        remoteLoaders.put("auth", new GrpcRemoteLoader(new RemoteServiceAddress("auth", AUTH_SERVICE_HOST, AUTH_SERVICE_PORT)));
        remoteLoaders.put("shipping", new GrpcRemoteLoader(new RemoteServiceAddress("shipping", SHIPPING_SERVICE_HOST, SHIPPING_SERVICE_PORT)));
        remoteLoaders.put("billing", new GrpcRemoteLoader(new RemoteServiceAddress("billing", BILLING_SERVICE_HOST, BILLING_SERVICE_PORT)));

        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .withLocalLoader(new LocalBusinessContextLoader()
            {
                @Override
                public Set<BusinessContext> load()
                {
                    return new HashSet<>(Arrays.asList(
                        BusinessContext.builder()
                            .withIdentifier(BusinessContextIdentifier.parse("order.valid"))
                            .withIncludedContext(BusinessContextIdentifier.parse("user.validEmail"))
                            .withIncludedContext(BusinessContextIdentifier.parse("shipping.correctAddress"))
                            .withIncludedContext(BusinessContextIdentifier.parse("billing.correctAddress"))
                            .build(),
                        BusinessContext.builder()
                            .withIdentifier(BusinessContextIdentifier.parse("order.create"))
                            .withIncludedContext(BusinessContextIdentifier.parse("auth.userLoggedIn"))
                            .withIncludedContext(BusinessContextIdentifier.parse("order.valid"))
                            // TODO: preconditions, postconditions
                            .build(),
                        BusinessContext.builder()
                            .withIdentifier(BusinessContextIdentifier.parse("order.changeState"))
                            .withIncludedContext(BusinessContextIdentifier.parse("auth.employeeLoggedIn"))
                            .withIncludedContext(BusinessContextIdentifier.parse("order.valid"))
                            // TODO: preconditions, postconditions
                            .build(),
                        BusinessContext.builder()
                            .withIdentifier(BusinessContextIdentifier.parse("order.listAll"))
                            .withIncludedContext(BusinessContextIdentifier.parse("auth.employeeLoggedIn"))
                            // TODO: preconditions, postconditions
                            .build(),
                        BusinessContext.builder()
                            .withIdentifier(BusinessContextIdentifier.parse("order.addToShoppingCart"))
                            .withIncludedContext(BusinessContextIdentifier.parse("auth.userLoggedIn"))
                            .withIncludedContext(BusinessContextIdentifier.parse("product.hidden"))
                            .withIncludedContext(BusinessContextIdentifier.parse("product.stock"))
                            .withPrecondition(Precondition.builder()
                                .withName("Shopping cart must contain less than 10 items")
                                .withCondition(new LessThan( // The rule is validated before the item is added
                                    new ObjectPropertyReference<>("shoppingCart", "itemCount", ExpressionType.NUMBER),
                                    new Constant<>(new BigDecimal("10"), ExpressionType.NUMBER)
                                ))
                                .build())
                            .build()
                    ));
                }
            })
            .withRemoteLoader(new RemoteBusinessContextLoader(remoteLoaders))
            .build();

        registry.initialize();

        return registry;
    }

    @Bean
    public static BusinessContextWeaver businessRuleEvaluator(final BusinessContextRegistry businessContextRegistry)
    {
        return new BusinessContextWeaver(businessContextRegistry);
    }

    @Bean
    public static BusinessContextAspect businessContextAspect(final BusinessContextWeaver weaver)
    {
        return new BusinessContextAspect(weaver);
    }

}
