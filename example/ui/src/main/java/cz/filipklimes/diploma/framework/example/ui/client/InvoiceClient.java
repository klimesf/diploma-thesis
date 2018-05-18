package cz.filipklimes.diploma.framework.example.ui.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.filipklimes.diploma.framework.example.ui.business.Address;
import cz.filipklimes.diploma.framework.example.ui.controller.response.ErrorResponse;
import cz.filipklimes.diploma.framework.example.ui.exception.CouldNotCreateInvoiceException;
import cz.filipklimes.diploma.framework.example.ui.facade.SignedUser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class InvoiceClient
{

    private static Logger log = LoggerFactory.getLogger(InvoiceClient.class);

    private final SignedUser signedUser;

    public InvoiceClient(final SignedUser signedUser)
    {
        this.signedUser = signedUser;
    }

    public void createInvoice(final Address billing) throws CouldNotCreateInvoiceException
    {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost("http://billing:5505/invoices");
            if (signedUser.isAnyoneSignedIn()) {
                request.addHeader("X-User-Id", String.valueOf(signedUser.getCurrentlyLoggedUser().getId()));
                request.addHeader("X-User-Role", signedUser.getCurrentlyLoggedUser().getRole());
            }

            String json = String.format(
                "{\"billing\":{\"country\":%s,\"city\":%s,\"street\":%s,\"postal\":%s}}",
                ClientHelper.jsonField(billing.getCountry()),
                ClientHelper.jsonField(billing.getCity()),
                ClientHelper.jsonField(billing.getStreet()),
                ClientHelper.jsonField(billing.getPostal())
            );
            request.setEntity(new StringEntity(json));
            request.setHeader("Content-type", "application/json");

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                log.debug(String.format("Created invoice, HTTP status %d", statusCode));

                ObjectMapper objectMapper = new ObjectMapper();
                if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                    ErrorResponse errorResponse = objectMapper.readValue(response.getEntity().getContent(), ErrorResponse.class);
                    throw new CouldNotCreateInvoiceException(errorResponse.getMessage());
                }

                if (statusCode != HttpStatus.ACCEPTED.value()) {
                    throw new RuntimeException(String.format("Could not create invoice: status code %d", statusCode));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not create invoice", e);
        }
    }

}
