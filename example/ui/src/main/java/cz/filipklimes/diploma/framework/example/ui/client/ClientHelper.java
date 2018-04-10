package cz.filipklimes.diploma.framework.example.ui.client;

import java.util.*;

public class ClientHelper
{

    public static String jsonField(final String value)
    {
        return Optional.ofNullable(value)
            .filter(s -> !s.isEmpty())
            .map(s -> "\"" + s + "\"")
            .orElse("null");
    }

}
