package cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf;

import cz.filipklimes.diploma.framework.businessContext.provider.BusinessContextProvider;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProtobufBusinessContextServer implements Runnable
{

    private final BusinessContextProvider provider;
    private final int port;
    private final ExecutorService executor;

    public ProtobufBusinessContextServer(final BusinessContextProvider provider, final int port)
    {
        this.provider = Objects.requireNonNull(provider);
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void run()
    {
        try (ServerSocket socket = new ServerSocket(port)) {
            while (true) {
                Runnable connection = new ProtobufBusinessContextConnection(socket.accept(), provider);
                executor.submit(connection);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
