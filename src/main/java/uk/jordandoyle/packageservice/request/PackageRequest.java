package uk.jordandoyle.packageservice.request;

import java.util.Set;

public class PackageRequest {
    private String name;
    private String description;
    private Set<String> products;

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Set<String> getProducts() {
        return this.products;
    }
}
