package cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProtobufBusinessRulesServer implements Runnable
{

    private final BusinessContextRegistry registry;
    private final int port;
    private final ExecutorService executor;

    public ProtobufBusinessRulesServer(final BusinessContextRegistry registry, final int port)
    {
        this.registry = Objects.requireNonNull(registry);
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void run()
    {
        try (ServerSocket socket = new ServerSocket(port)) {
            while (true) {
                Runnable connection = new ProtobufBusinessRulesConnection(socket.accept(), registry);
                executor.submit(connection);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
