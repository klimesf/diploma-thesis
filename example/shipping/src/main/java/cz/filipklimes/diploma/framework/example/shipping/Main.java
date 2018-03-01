package cz.filipklimes.diploma.framework.example.shipping;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.GrpcBusinessContextServer;

public class Main
{

    private static final int PORT = 5552;

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
        System.out.println("Shipping rules server running");
        t.join();
    }

}
