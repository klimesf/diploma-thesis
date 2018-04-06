package cz.filipklimes.diploma.framework.example.order.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.filipklimes.diploma.framework.example.order.business.User;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class UserClient
{

    private static Logger log = LoggerFactory.getLogger(UserClient.class);

    public User getUser(final Integer userId)
    {
        Objects.requireNonNull(userId);
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet(String.format("http://user:5503/users/%d", userId));

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Fetched user %d, HTTP status %d", userId, statusCode));

                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    return null;
                }

                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getEntity().getContent(), User.class);
            }

        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not load user %d", userId), e);
        }
    }

}
