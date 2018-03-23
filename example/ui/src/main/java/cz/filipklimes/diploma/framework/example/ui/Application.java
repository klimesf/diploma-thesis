package cz.filipklimes.diploma.framework.example.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class Application
{

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @Profile("local")
    @ComponentScan(lazyInit = true)
    static class LocalConfig
    {

    }

}
