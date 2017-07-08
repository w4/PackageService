package uk.jordandoyle.packageservice.repository;

import org.springframework.stereotype.Repository;
import uk.jordandoyle.packageservice.domain.Product;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data store/container class to store all our known products from our downstream service.
 */
@Repository
public final class ProductRepository {
    /**
     * All the products we know about
     */
    private final Map<String, Product> PRODUCTS = new HashMap<>();

    /**
     * Adds or updates our product store with this product.
     *
     * @param newProduct product to add/update
     */
    public void addProduct(Product newProduct) {
        if (this.PRODUCTS.containsKey(newProduct.getId())) {
            // we don't want to replace our Product instance if the product changes because then our Packages will
            // hold a reference to a different object.
            Product productRef = this.PRODUCTS.get(newProduct.getId());

            // try to copy over fields from our new product to our old.
            for (Field field : Product.class.getDeclaredFields()) {
                field.setAccessible(true);

                try {
                    field.set(productRef, field.get(newProduct));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            // we don't already know about this project so just add it normally
            this.PRODUCTS.put(newProduct.getId(), newProduct);
        }
    }

    /**
     * Add a collection of products to our product store.
     *
     * @param products products to add to our store
     */
    public void addProducts(Product[] products) {
        for (Product p : products) {
            this.addProduct(p);
        }
    }

    /**
     * Get all products that we know about.
     *
     * @return a list of products
     */
    public Set<Product> getProducts() {
        return new HashSet<>(this.PRODUCTS.values());
    }

    /**
     * Get a product by its unique identifier.
     *
     * @param id id to find product by
     * @return product requested or {@code null} if it couldn't be found
     */
    public Product getProductById(String id) {
        return this.PRODUCTS.get(id);
    }
}
