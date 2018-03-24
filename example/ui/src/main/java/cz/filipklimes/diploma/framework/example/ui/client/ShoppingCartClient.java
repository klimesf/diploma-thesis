package cz.filipklimes.diploma.framework.example.ui.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotAddShoppingCartItemException;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class ShoppingCartClient
{

    private static Logger log = LoggerFactory.getLogger(ShoppingCartClient.class);

    public List<ShoppingCartItem> listCartItems()
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet("http://localhost:5501/shopping-cart");

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
            HttpPost request = new HttpPost("http://localhost:5501/shopping-cart");
            String json = String.format("{\"productId\":%d, \"quantity\":%d}", productId, quantity);
            request.setEntity(new StringEntity(json));
            request.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Fetched products, HTTP status %d", statusCode));

                ObjectMapper objectMapper = new ObjectMapper();
                if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                    AddProductToCartErrorResponse errorResponse = objectMapper.readValue(response.getEntity().getContent(), AddProductToCartErrorResponse.class);
                    throw new CouldNotAddShoppingCartItemException(errorResponse.getMessage());
                }

                if (statusCode != HttpStatus.ACCEPTED.value()) {
                    throw new RuntimeException(String.format("Could not add product to shopping cart: status code %d", statusCode));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not load products", e);
        }
    }

    public static final class ShoppingCartItem
    {

        @Getter
        private Integer productId;

        @Getter
        private Integer quantity;

        public ShoppingCartItem()
        {
        }

        public ShoppingCartItem(final Integer productId, final Integer quantity)
        {
            this.productId = productId;
            this.quantity = quantity;
        }

    }

    public static final class ListShoppingCartItemsResponse
    {

        @Getter
        private Integer count;

        @Getter
        private List<ShoppingCartItem> items;

        public ListShoppingCartItemsResponse()
        {
        }

        public ListShoppingCartItemsResponse(final Integer count, final List<ShoppingCartItem> items)
        {
            this.count = count;
            this.items = Collections.unmodifiableList(items);
        }

    }

    public static final class AddProductToCartErrorResponse
    {

        @Getter
        private String message;

        public AddProductToCartErrorResponse()
        {
        }

        private AddProductToCartErrorResponse(final String message)
        {
            this.message = message;
        }

    }

}
