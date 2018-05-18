package cz.filipklimes.diploma.framework.example.ui.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.filipklimes.diploma.framework.example.ui.business.Address;
import cz.filipklimes.diploma.framework.example.ui.business.Order;
import cz.filipklimes.diploma.framework.example.ui.controller.response.ErrorResponse;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotAddShoppingCartItemException;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotCreateOrderException;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotListOrdersException;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotMarkDeliveredException;
import cz.filipklimes.diploma.framework.example.ui.facade.SignedUser;
import lombok.Getter;
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
public class OrderClient
{

    private static Logger log = LoggerFactory.getLogger(OrderClient.class);

    private final SignedUser signedUser;

    @Autowired
    public OrderClient(final SignedUser signedUser)
    {
        this.signedUser = signedUser;
    }

    public List<ShoppingCartItem> listCartItems()
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet("http://order:5501/shopping-cart");
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Fetched shopping cart items, HTTP status %d", statusCode));

                if (statusCode != HttpStatus.OK.value()) {
                    throw new RuntimeException(String.format("Could not load shopping cart items: status code %d", statusCode));
                }

                ObjectMapper objectMapper = new ObjectMapper();
                ListShoppingCartItemsResponse items = objectMapper.readValue(response.getEntity().getContent(), ListShoppingCartItemsResponse.class);
                return items.getItems();
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not load shopping cart items", e);
        }
    }

    public void addItem(final Integer productId, final Integer quantity) throws CouldNotAddShoppingCartItemException
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost("http://order:5501/shopping-cart");
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

            String json = String.format("{\"productId\":%d, \"quantity\":%d}", productId, quantity);
            request.setEntity(new StringEntity(json));
            request.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Added product to shopping cart, HTTP status %d", statusCode));

                ObjectMapper objectMapper = new ObjectMapper();
                if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                    ErrorResponse errorResponse = objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class);
                    throw new CouldNotAddShoppingCartItemException(errorResponse.getMessage());
                }

                if (statusCode != HttpStatus.ACCEPTED.value()) {
                    throw new RuntimeException(String.format("Could not add product to shopping cart: status code %d", statusCode));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not add item to shopping cart", e);
        }
    }

    public List<Order> listOrders() throws CouldNotListOrdersException
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet("http://order:5501/orders");
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Fetched orders, HTTP status %d", statusCode));

                ObjectMapper objectMapper = new ObjectMapper();
                if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                    ErrorResponse errorResponse = objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class);
                    throw new CouldNotListOrdersException(errorResponse.getMessage());
                }

                if (statusCode != HttpStatus.OK.value()) {
                    throw new RuntimeException(String.format("Could not load orders: status code %d", statusCode));
                }

                Order[] orders = objectMapper.readValue(response.getEntity().getContent(), Order[].class);
                return Arrays.asList(orders);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not load orders", e);
        }
    }

    public void createOrder(final Address shipping, final Address billing) throws CouldNotCreateOrderException
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost("http://order:5501/orders");
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

            String json = String.format(
                "{\"shipping\":{\"country\":%s,\"city\":%s,\"street\":%s,\"postal\":%s}," +
                    "\"billing\":{\"country\":%s,\"city\":%s,\"street\":%s,\"postal\":%s}}",
                ClientHelper.jsonField(shipping.getCountry()),
                ClientHelper.jsonField(shipping.getCity()),
                ClientHelper.jsonField(shipping.getStreet()),
                ClientHelper.jsonField(shipping.getPostal()),
                ClientHelper.jsonField(billing.getCountry()),
                ClientHelper.jsonField(billing.getCity()),
                ClientHelper.jsonField(billing.getStreet()),
                ClientHelper.jsonField(billing.getPostal())
            );
            request.setEntity(new StringEntity(json));
            request.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Created order, HTTP status %d", statusCode));

                ObjectMapper objectMapper = new ObjectMapper();
                if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                    ErrorResponse errorResponse = objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class);
                    throw new CouldNotCreateOrderException(errorResponse.getMessage());
                }

                if (statusCode != HttpStatus.ACCEPTED.value()) {
                    throw new RuntimeException(String.format("Could not create order: status code %d", statusCode));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not create order", e);
        }
    }

    public void markDelivered(final Integer id) throws CouldNotMarkDeliveredException
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(String.format("http://order:5501/orders/%d/status", id));
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

            String json = String.format(
                "{\"status\":%s}",
                ClientHelper.jsonField("DELIVERED")
            );
            request.setEntity(new StringEntity(json));
            request.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Could not mark order as delivered, HTTP status %d", statusCode));

                ObjectMapper objectMapper = new ObjectMapper();
                if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                    ErrorResponse errorResponse = objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class);
                    throw new CouldNotMarkDeliveredException(errorResponse.getMessage());
                }

                if (statusCode != HttpStatus.ACCEPTED.value()) {
                    throw new RuntimeException(String.format("Could not mark order as delivered: status code %d", statusCode));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not mark order as delivered", e);
        }
    }

    public static final class ShoppingCartItem
    {

        @Getter
        private Integer productId;

        @Getter
        private Integer quantity;

    }

    private static final class ListShoppingCartItemsResponse
    {

        @Getter
        private Integer count;

        @Getter
        private List<ShoppingCartItem> items;

    }

}
