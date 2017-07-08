package uk.jordandoyle.packageservice.service;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.common.base.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.jordandoyle.packageservice.domain.Product;
import uk.jordandoyle.packageservice.repository.ProductRepository;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Periodically synchronises with the ProductService so we have up-to-date product information.
 */
@Service
public class ProductServiceSynchroniser {
    /**
     * Class logger
     */
    private final Logger LOGGER = LoggerFactory.getLogger(ProductServiceSynchroniser.class);

    /**
     * Downstream endpoint we need to hit for product data
     */
    private final GenericUrl PRODUCT_ENDPOINT = new GenericUrl("https://product-service.herokuapp" +
            ".com/api/v1/products");

    /**
     * Request factory for building HTTP requests for the downstream service
     */
    private final HttpRequestFactory requestFactory = new NetHttpTransport()
            .createRequestFactory(request -> request.setParser(new JsonObjectParser(new JacksonFactory())));

    @Autowired
    private ProductRepository productRepository;

    /**
     * Synchronise our products with the downstream service.
     */
    @Scheduled(fixedDelay = 10000)
    @Async
    public void synchronise() throws IOException {
        this.LOGGER.info("Grabbing latest product list from downstream Product Service");
        Stopwatch stopwatch = Stopwatch.createStarted();

        // build our http request
        HttpRequest request = this.requestFactory.buildGetRequest(this.PRODUCT_ENDPOINT);

        // don't spam the downstream service with requests if we can't hit it
        request.setUnsuccessfulResponseHandler(new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff()));

        // downstream service requires basic auth
        request.getHeaders().setBasicAuthentication("user", "pass");

        // execute request (we're on a separate thread to main so we can run synchronously)
        HttpResponse response = request.execute();

        // parse the result and add them to the product container - new products can be added but never removed
        Product[] result = response.parseAs(Product[].class);

        this.productRepository.addProducts(result);

        stopwatch.stop();
        this.LOGGER.info("Grabbed and parsed all products in {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
}
