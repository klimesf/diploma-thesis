package cz.filipklimes.diploma.framework.example.billing;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.provider.server.grpc.GrpcBusinessContextServer;

public class Main
{

    private static final int PORT = 5551;

    public static void main(String[] args) throws InterruptedException
    {
        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .build();

        registry.initialize();

        GrpcBusinessContextServer server = new GrpcBusinessContextServer(
            registry,
            PORT
        );

        Thread t = new Thread(server);
        t.start();
        System.out.println("Billing rules server running");
        t.join();
    }

}
