package cz.filipklimes.diploma.framework.example.shipping;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;
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
        List<InputStream> streams = new ArrayList<>();
        streams.add(Application.class.getResourceAsStream("/business-contexts/correctAddress.xml"));

        return BusinessContextRegistry.builder()
            .withLocalLoader(new BusinessContextXmlLoader(streams))
            .withRemoteLoader(new RemoteBusinessContextLoader(new HashMap<>()))
            .build();
    }

}
