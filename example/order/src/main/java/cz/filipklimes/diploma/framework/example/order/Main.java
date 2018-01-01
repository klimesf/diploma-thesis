package cz.filipklimes.diploma.framework.example.order;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.loader.LocalDroolsBusinessContextLoader;
import cz.filipklimes.diploma.framework.businessContext.loader.RemoteBusinessContextLoader;

public class Main
{

    public static void main(String[] args)
    {
        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .addLoader(new LocalDroolsBusinessContextLoader())
            .addLoader(new RemoteBusinessContextLoader("localhost", 5551)) // Billing
            .addLoader(new RemoteBusinessContextLoader("localhost", 5552)) // Shipping
            .addLoader(new RemoteBusinessContextLoader("localhost", 5553)) // User
            .build();

        registry.getAllRules().forEach(rule -> System.out.println(rule.getName()));
    }

}
