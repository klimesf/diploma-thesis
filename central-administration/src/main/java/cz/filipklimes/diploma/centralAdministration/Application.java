package cz.filipklimes.diploma.centralAdministration;

import cz.filipklimes.diploma.centralAdministration.businessContext.BusinessContextEditor;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteServiceAddress;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.grpc.GrpcRemoteLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.*;

@SpringBootApplication
public class Application
{

    private static String ORDER_SERVICE_HOST = "localhost";
    private static int ORDER_SERVICE_PORT = 5551;

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
    public BusinessContextEditor createBusinessContextEditor()
    {
        RemoteLoader orderRemoteLoader = new GrpcRemoteLoader(new RemoteServiceAddress("order", ORDER_SERVICE_HOST, ORDER_SERVICE_PORT));
        RemoteLoader authRemoteLoader = new GrpcRemoteLoader(new RemoteServiceAddress("auth", AUTH_SERVICE_HOST, AUTH_SERVICE_PORT));
        RemoteLoader productRemoteLoader = new GrpcRemoteLoader(new RemoteServiceAddress("product", PRODUCT_SERVICE_HOST, PRODUCT_SERVICE_PORT));
        RemoteLoader userRemoteLoader = new GrpcRemoteLoader(new RemoteServiceAddress("user", USER_SERVICE_HOST, USER_SERVICE_PORT));
        RemoteLoader shippingRemoteLoader = new GrpcRemoteLoader(new RemoteServiceAddress("shipping", SHIPPING_SERVICE_HOST, SHIPPING_SERVICE_PORT));
        RemoteLoader billingRemoteLoader = new GrpcRemoteLoader(new RemoteServiceAddress("billing", BILLING_SERVICE_HOST, BILLING_SERVICE_PORT));

        return new BusinessContextEditor(Arrays.asList(
            orderRemoteLoader,
            authRemoteLoader,
            productRemoteLoader,
            userRemoteLoader,
            shippingRemoteLoader,
            billingRemoteLoader
        ));
    }

}
