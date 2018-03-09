package cz.filipklimes.diploma.framework.businessContext.provider.server.grpc;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;

import java.io.*;
import java.util.*;

public class GrpcBusinessContextServer implements Runnable
{

    private final BusinessContextRegistry registry;
    private final int port;

    public GrpcBusinessContextServer(final BusinessContextRegistry registry, final int port)
    {
        this.registry = Objects.requireNonNull(registry);
        this.port = port;
    }

    @Override
    public void run()
    {
        try {
            GrpcBusinessContextServerRunnable server = new GrpcBusinessContextServerRunnable(registry, port);
            server.start();
            server.blockUntilShutdown();

        } catch (InterruptedException ignore) {
        } catch (IOException e) {
            throw new RuntimeException("Could not serve business context", e);
        }
    }

}
