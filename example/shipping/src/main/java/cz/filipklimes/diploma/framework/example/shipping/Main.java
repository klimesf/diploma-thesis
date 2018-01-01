package cz.filipklimes.diploma.framework.example.shipping;

import cz.filipklimes.diploma.framework.businessContext.loader.LocalDroolsBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.provider.DroolsBusinessContextProvider;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.ProtobufBusinessContextServer;

public class Main
{

    private static final int PORT = 5552;

    public static void main(String[] args) throws InterruptedException
    {
        ProtobufBusinessContextServer server = new ProtobufBusinessContextServer(
            new DroolsBusinessContextProvider(new LocalDroolsBusinessContextLoader()),
            PORT
        );
        Thread t = new Thread(server);
        t.start();
        System.out.println("Shipping rules server running");
        t.join();
    }

}
