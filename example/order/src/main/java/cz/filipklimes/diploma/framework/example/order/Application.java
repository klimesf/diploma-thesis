package cz.filipklimes.diploma.framework.example.order;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.aop.BusinessContextAspect;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteServiceAddress;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.grpc.GrpcRemoteLoader;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.GrpcBusinessContextServer;
import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessContextWeaver;
import cz.filipklimes.diploma.framework.businessContext.xml.BusinessContextXmlLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.*;
import java.util.*;

@SpringBootApplication
public class Application
{

    private static final int BUSINESS_CONTEXT_SERVER_PORT = 5551;

    private static String AUTH_SERVICE_HOST = "user";
    private static int AUTH_SERVICE_PORT = 5553;

    private static String PRODUCT_SERVICE_HOST = "product";
    private static int PRODUCT_SERVICE_PORT = 5552;

    private static String USER_SERVICE_HOST = "user";
    private static int USER_SERVICE_PORT = 5553;

    private static String SHIPPING_SERVICE_HOST = "shipping";
    private static int SHIPPING_SERVICE_PORT = 5554;

    private static String BILLING_SERVICE_HOST = "billing";
    private static int BILLING_SERVICE_PORT = 5555;

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
        Map<String, RemoteLoader> remoteLoaders = new HashMap<>();
        remoteLoaders.put("product", new GrpcRemoteLoader(new RemoteServiceAddress("product", PRODUCT_SERVICE_HOST, PRODUCT_SERVICE_PORT)));
        remoteLoaders.put("user", new GrpcRemoteLoader(new RemoteServiceAddress("user", USER_SERVICE_HOST, USER_SERVICE_PORT)));
        remoteLoaders.put("auth", new GrpcRemoteLoader(new RemoteServiceAddress("auth", AUTH_SERVICE_HOST, AUTH_SERVICE_PORT)));
        remoteLoaders.put("shipping", new GrpcRemoteLoader(new RemoteServiceAddress("shipping", SHIPPING_SERVICE_HOST, SHIPPING_SERVICE_PORT)));
        remoteLoaders.put("billing", new GrpcRemoteLoader(new RemoteServiceAddress("billing", BILLING_SERVICE_HOST, BILLING_SERVICE_PORT)));

        List<InputStream> streams = new ArrayList<>();
        streams.add(Application.class.getResourceAsStream("/business-contexts/addToShoppingCart.xml"));
        streams.add(Application.class.getResourceAsStream("/business-contexts/changeState.xml"));
        streams.add(Application.class.getResourceAsStream("/business-contexts/create.xml"));
        streams.add(Application.class.getResourceAsStream("/business-contexts/listAll.xml"));
        streams.add(Application.class.getResourceAsStream("/business-contexts/valid.xml"));

        return BusinessContextRegistry.builder()
            .withLocalLoader(new BusinessContextXmlLoader(streams))
            .withRemoteLoader(new RemoteBusinessContextLoader(remoteLoaders))
            .build();
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
