package cz.filipklimes.diploma.framework.example.user;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.GrpcBusinessContextServer;

public class Main
{

    private static final int PORT = 5553;

    public static void main(String[] args) throws InterruptedException
    {
        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .build();

        GrpcBusinessContextServer server = new GrpcBusinessContextServer(
            registry,
            PORT
        );

        registry.initialize();

        Thread t = new Thread(server);
        t.start();
        System.out.println("User rules server running");
        t.join();
    }

}
