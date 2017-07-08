package uk.jordandoyle.packageservice.domain;

import com.google.api.client.util.Key;

public final class Product {
    /**
     * ID of this product given to us by the
     * external API
     */
    @Key
    private String id;

    /**
     * The name of this product
     */
    @Key
    private String name;

    /**
     * The price of this product in USD cents
     */
    @Key("usdPrice")
    private int price;

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }
}
