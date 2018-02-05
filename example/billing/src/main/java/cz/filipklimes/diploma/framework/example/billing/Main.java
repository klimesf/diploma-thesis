package cz.filipklimes.diploma.framework.example.billing;

import cz.filipklimes.diploma.framework.businessContext.loader.LocalDroolsBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.provider.DroolsBusinessContextProvider;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.ProtobufBusinessContextServer;

public class Main
{

    private static final int PORT = 5551;

    public static void main(String[] args) throws InterruptedException
    {
        ProtobufBusinessContextServer server = new ProtobufBusinessContextServer(
            new DroolsBusinessContextProvider(new LocalDroolsBusinessContextLoader()),
            PORT
        );
        Thread t = new Thread(server);
        t.start();
        System.out.println("Billing rules server running");
        t.join();
    }

}