package cz.filipklimes.diploma.framework.example.ui.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.filipklimes.diploma.framework.example.ui.business.User;
import cz.filipklimes.diploma.framework.example.ui.controller.response.ErrorResponse;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotCreateUserException;
import cz.filipklimes.diploma.framework.example.ui.facade.SignedUser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class UserClient
{

    private static Logger log = LoggerFactory.getLogger(UserClient.class);

    private final SignedUser signedUser;

    @Autowired
    public UserClient(final SignedUser signedUser)
    {
        this.signedUser = signedUser;
    }

    public List<User> listUsers()
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet("http://localhost:5503/users/");
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Fetched products, HTTP status %d", statusCode));

                if (statusCode != HttpStatus.OK.value()) {
                    throw new RuntimeException(String.format("Could not load users: status code %d", statusCode));
                }

                ObjectMapper objectMapper = new ObjectMapper();
                User[] products = objectMapper.readValue(response.getEntity().getContent(), User[].class);
                return Arrays.asList(products);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not load users", e);
        }
    }

    public User getUser(final Integer userId)
    {
        Objects.requireNonNull(userId);
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet(String.format("http://localhost:5503/users/%d", userId));
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

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

    public User register(final String name, final String email) throws CouldNotCreateUserException
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(email);
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost("http://localhost:5503/users");
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

            String json = String.format("{\"name\":\"%s\", \"email\":\"%s\"}", name, email);
            request.setEntity(new StringEntity(json));
            request.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Registered a new user, HTTP status %d", statusCode));

                ObjectMapper objectMapper = new ObjectMapper();
                if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                    ErrorResponse errorResponse = objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class);
                    throw new CouldNotCreateUserException(errorResponse.getMessage());
                }

                if (statusCode != HttpStatus.OK.value()) {
                    throw new RuntimeException(String.format("Could not register user: status code %d", statusCode));
                }

                return objectMapper.readValue(response.getEntity().getContent(), User.class);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not register new user", e);
        }
    }

}
