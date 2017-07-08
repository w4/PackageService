package uk.jordandoyle.packageservice.domain;

import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;

import java.math.BigDecimal;
import java.util.Map;

public class ExchangeRate {
    /**
     * Base currency the exchange rates are based on
     */
    @Key
    private String base;

    /**
     * Date these exchange rates are for
     */
    @Key
    private DateTime date;

    /**
     * Exchange rates
     */
    @Key
    private Map<String, BigDecimal> rates;

    public String getBase() {
        return this.base;
    }

    public DateTime getDate() {
        return this.date;
    }

    public Map<String, BigDecimal> getRates() {
        return this.rates;
    }
}
