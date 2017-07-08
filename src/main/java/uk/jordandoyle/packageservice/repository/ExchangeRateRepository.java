package uk.jordandoyle.packageservice.repository;

import com.google.api.client.util.DateTime;
import org.springframework.stereotype.Repository;
import uk.jordandoyle.packageservice.domain.ExchangeRate;

import java.math.BigDecimal;
import java.util.Map;

@Repository
public class ExchangeRateRepository {
    private ExchangeRate currentExchangeRate;

    /**
     * Set the current exchange rates
     *
     * @param exchangeRate
     */
    public void setCurrentExchangeRate(ExchangeRate exchangeRate) {
        this.currentExchangeRate = exchangeRate;
    }

    /**
     * Proxy method to {@link ExchangeRate#getBase()}.
     *
     * @return base currency the exchange rates are based on
     */
    public String getBase() {
        return this.currentExchangeRate.getBase();
    }

    /**
     * Proxy method to {@link ExchangeRate#getDate()}.
     *
     * @return date these exchange rates are for
     */
    public DateTime getDate() {
        return this.currentExchangeRate.getDate();
    }

    /**
     * Proxy method to {@link ExchangeRate#getRates()}.
     *
     * @return exchange rates for {@link ExchangeRate#base}
     */
    public Map<String, BigDecimal> getRates() {
        return this.currentExchangeRate.getRates();
    }
}
