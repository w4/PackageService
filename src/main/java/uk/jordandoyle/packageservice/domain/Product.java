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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Product && ((Product) obj).getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        int result = this.id.hashCode();
        result = 31 * result + this.name.hashCode();
        result = 31 * result + Integer.hashCode(this.price);
        return result;
    }
}
