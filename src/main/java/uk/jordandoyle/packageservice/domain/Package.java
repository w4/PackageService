package uk.jordandoyle.packageservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A package of products ready to be sent out to a customer.
 */
public class Package {
    /**
     * UUID of this package
     */
    private UUID uuid;

    /**
     * Name of this package of products
     */
    private String name;

    /**
     * Description of this package
     */
    private String description;

    /**
     * List of products in this package.
     */
    @JsonIgnore
    private Set<Product> products = new HashSet<>();

    public Package() {
        this.uuid = UUID.randomUUID();
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
