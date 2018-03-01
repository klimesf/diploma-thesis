package cz.filipklimes.diploma.framework.businessContext.loader.remote;

import lombok.Getter;

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

}
