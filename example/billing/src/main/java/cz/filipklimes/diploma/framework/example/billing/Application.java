package cz.filipklimes.diploma.framework.example.billing;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.RemoteServiceAddress;
import cz.filipklimes.diploma.framework.businessContext.loader.remote.grpc.GrpcRemoteLoader;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.GrpcBusinessContextServer;
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

    private static String AUTH_SERVICE_HOST = "user";
    private static int AUTH_SERVICE_PORT = 5553;

    private static final int BUSINESS_CONTEXT_SERVER_PORT = 5555;

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
        remoteLoaders.put("auth", new GrpcRemoteLoader(new RemoteServiceAddress("auth", AUTH_SERVICE_HOST, AUTH_SERVICE_PORT)));

        List<InputStream> streams = new ArrayList<>();
        streams.add(Application.class.getResourceAsStream("/business-contexts/correctAddress.xml"));
        streams.add(Application.class.getResourceAsStream("/business-contexts/createInvoice.xml"));

        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .withLocalLoader(new BusinessContextXmlLoader(streams))
            .withRemoteLoader(new RemoteBusinessContextLoader(remoteLoaders))
            .build();

        return registry;
    }

}
