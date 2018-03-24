package cz.filipklimes.diploma.framework.example.order.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.filipklimes.diploma.framework.example.order.business.Product;
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
public class ProductClient
{

    private static Logger log = LoggerFactory.getLogger(ProductClient.class);

    public List<Product> listProducts()
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet("http://localhost:5502/");

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Fetched products, HTTP status %d", statusCode));

                if (statusCode != HttpStatus.OK.value()) {
                    throw new RuntimeException(String.format("Could not load products: status code %d", statusCode));
                }

                ObjectMapper objectMapper = new ObjectMapper();
                Product[] products = objectMapper.readValue(response.getEntity().getContent(), Product[].class);
                return Arrays.asList(products);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not load products", e);
        }
    }

    public Product getProduct(final Integer productId)
    {
        Objects.requireNonNull(productId);
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet(String.format("http://localhost:5502/%d", productId));

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Fetched product %d, HTTP status %d", productId, statusCode));

                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    return null;
                }

                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getEntity().getContent(), Product.class);
            }

        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not load product %d", productId), e);
        }
    }

}
