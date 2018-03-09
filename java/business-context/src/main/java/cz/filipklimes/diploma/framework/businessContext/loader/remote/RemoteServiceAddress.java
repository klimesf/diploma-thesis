package cz.filipklimes.diploma.framework.businessContext.loader.remote;

import lombok.Getter;

import java.util.*;

public class RemoteServiceAddress
{

    @Getter
    private final String name;

    @Getter
    private final String host;

    @Getter
    private final int port;

    public RemoteServiceAddress(final String name, final String host, final int port)
    {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean equals(final Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RemoteServiceAddress that = (RemoteServiceAddress) o;
        return port == that.getPort()
            && Objects.equals(name, that.getName())
            && Objects.equals(host, that.getHost());
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, host, port);
    }

}
