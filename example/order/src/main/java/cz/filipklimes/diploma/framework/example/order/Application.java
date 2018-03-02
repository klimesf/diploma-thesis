package cz.filipklimes.diploma.framework.example.order;

import cz.filipklimes.diploma.framework.businessContext.BusinessContextRegistry;
import cz.filipklimes.diploma.framework.businessContext.weaver.BusinessContextWeaver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application
{

    private static String BILLING_HOST = "localhost";
    private static int BILLING_PORT = 5551;

    private static String SHIPPING_HOST = "localhost";
    private static int SHIPPING_PORT = 5552;

    private static String USER_HOST = "localhost";
    private static int USER_PORT = 5553;

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public static BusinessContextRegistry businessContextRegistry()
    {
        BusinessContextRegistry registry = BusinessContextRegistry.builder()
            .build();

        registry.initialize();

        return registry;
    }

    @Bean
    public static BusinessContextWeaver businessRuleEvaluator(final BusinessContextRegistry businessContextRegistry)
    {
        return new BusinessContextWeaver(businessContextRegistry);
    }

}
