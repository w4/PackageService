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
import uk.jordandoyle.packageservice.domain.ExchangeRate;
import uk.jordandoyle.packageservice.repository.ExchangeRateRepository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Synchronises with the Fixer downstream API to get the latest exchange rates.
 *
 * @see <a href="http://fixer.io/">http://fixer.io/</a>
 */
@Service
public class CurrencySynchroniser {
    /**
     * Class logger
     */
    private final Logger LOGGER = LoggerFactory.getLogger(CurrencySynchroniser.class);

    /**
     * Downstream endpoint we need to hit for product data
     */
    private final GenericUrl EXCHANGE_RATE_ENDPOINT = new GenericUrl("http://api.fixer.io/latest?base=USD");

    /**
     * Request factory for building HTTP requests for the downstream service
     */
    private final HttpRequestFactory requestFactory = new NetHttpTransport()
            .createRequestFactory(request -> request.setParser(new JsonObjectParser(new JacksonFactory())));

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    /**
     * The Fixer API updates its rates at "around" 4pm CET every day, we'll sync up with them at 4:15pm.
     */
    @Scheduled(cron = "0 15 16 * * *", zone = "CET")
    @PostConstruct // run this method on startup
    @Async
    public void synchroniser() throws IOException {
        this.LOGGER.info("Grabbing latest exchanges rates from downstream");
        Stopwatch stopwatch = Stopwatch.createStarted();

        // build our http request
        HttpRequest request = this.requestFactory.buildGetRequest(this.EXCHANGE_RATE_ENDPOINT);

        // don't spam the downstream service with requests if we can't hit it
        request.setUnsuccessfulResponseHandler(new HttpBackOffUnsuccessfulResponseHandler(new ExponentialBackOff()));

        // execute request (we're on a separate thread to main so we can run synchronously)
        HttpResponse response = request.execute();

        // convert JSON result to a Bean
        ExchangeRate exchangeRate = response.parseAs(ExchangeRate.class);

        // update the application exchange rate
        this.exchangeRateRepository.setCurrentExchangeRate(exchangeRate);

        stopwatch.stop();
        this.LOGGER.info("Grabbed and parsed exchange rates in {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
}
