package cz.filipklimes.diploma.framework.businessContext.loader.client.protobuf;

import cz.filipklimes.diploma.framework.businessContext.BusinessRule;
import cz.filipklimes.diploma.framework.businessContext.provider.server.protobuf.BusinessRulesProtos;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class ProtobufBusinessContextClient
{

    private final String host;
    private final int port;

    public ProtobufBusinessContextClient(final String host, final int port)
    {
        this.host = host;
        this.port = port;
    }

    /**
     * Recieves rules from protobuf business context server.
     *
     * @return Set of business rules.
     */
    public Set<BusinessRule> receiveRules()
    {
        try (
            Socket socket = new Socket(host, port);
            InputStream in = socket.getInputStream()
        ) {
            return BusinessRulesProtos.BusinessRulesMessage.parseFrom(in)
                .getRulesList().stream()
                .map(ruleMessage -> new BusinessRule(ruleMessage.getName(), ruleMessage.getPackageName()))
                .collect(Collectors.toSet());

        } catch (IOException e) {
            throw new RuntimeException("Could not load business rules", e);
        }
    }

}
