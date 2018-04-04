package cz.filipklimes.diploma.centralAdministration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application
{

    private static String AUTH_SERVICE_HOST = "localhost";
    private static int AUTH_SERVICE_PORT = 5553;

    private static String PRODUCT_SERVICE_HOST = "localhost";
    private static int PRODUCT_SERVICE_PORT = 5552;

    private static String USER_SERVICE_HOST = "localhost";
    private static int USER_SERVICE_PORT = 5553;

    private static String SHIPPING_SERVICE_HOST = "localhost";
    private static int SHIPPING_SERVICE_PORT = 5554;

    private static String BILLING_SERVICE_HOST = "localhost";
    private static int BILLING_SERVICE_PORT = 5555;

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

}
