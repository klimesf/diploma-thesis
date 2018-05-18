package cz.filipklimes.diploma.framework.example.ui.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.filipklimes.diploma.framework.example.ui.business.Product;
import cz.filipklimes.diploma.framework.example.ui.controller.response.ErrorResponse;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotChangePriceException;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotChangeStockException;
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
public class ProductClient
{

    private static Logger log = LoggerFactory.getLogger(ProductClient.class);

    private final SignedUser signedUser;

    @Autowired
    public ProductClient(final SignedUser signedUser)
    {
        this.signedUser = signedUser;
    }

    public List<Product> listProducts()
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet("http://product:5502/");
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

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
            HttpUriRequest request = new HttpGet(String.format("http://product:5502/%d", productId));
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

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

    public Product changePrice(final Integer productId, final String costPrice, final String sellPrice) throws CouldNotChangePriceException
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(String.format("http://product:5502/%d/price", productId));
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

            String json = String.format(
                "{\"costPrice\":%s, \"sellPrice\":%s}",
                ClientHelper.jsonField(costPrice),
                ClientHelper.jsonField(sellPrice)
            );
            request.setEntity(new StringEntity(json));
            request.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Changed price, HTTP status %d", statusCode));

                ObjectMapper objectMapper = new ObjectMapper();
                if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                    ErrorResponse errorResponse = objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class);
                    throw new CouldNotChangePriceException(errorResponse.getMessage());
                }

                if (statusCode != HttpStatus.OK.value()) {
                    throw new RuntimeException(String.format("Could not change price: status code %d", statusCode));
                }

                return objectMapper.readValue(response.getEntity().getContent(), Product.class);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not change price", e);
        }
    }

    public Product changeStock(final Integer productId, final String stockCount) throws CouldNotChangeStockException
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost(String.format("http://product:5502/%d/stock", productId));
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

            String json = String.format(
                "{\"stockCount\":%s}",
                ClientHelper.jsonField(stockCount)
            );
            request.setEntity(new StringEntity(json));
            request.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Changed stock, HTTP status %d", statusCode));

                ObjectMapper objectMapper = new ObjectMapper();
                if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                    ErrorResponse errorResponse = objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class);
                    throw new CouldNotChangeStockException(errorResponse.getMessage());
                }

                if (statusCode != HttpStatus.OK.value()) {
                    throw new RuntimeException(String.format("Could not change stock: status code %d", statusCode));
                }

                return objectMapper.readValue(response.getEntity().getContent(), Product.class);
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not change stock", e);
        }
    }

}
