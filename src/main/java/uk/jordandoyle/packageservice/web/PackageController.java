package uk.jordandoyle.packageservice.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.jordandoyle.packageservice.repository.ExchangeRateRepository;
import uk.jordandoyle.packageservice.repository.PackageRepository;
import uk.jordandoyle.packageservice.domain.Package;
import uk.jordandoyle.packageservice.domain.Product;
import uk.jordandoyle.packageservice.repository.ProductRepository;
import uk.jordandoyle.packageservice.request.PackageRequest;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@RestController
public class PackageController {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    /**
     * Get all packages.
     *
     * @return all packages that have been created
     */
    @RequestMapping(value = "/package", method = RequestMethod.GET)
    public Collection<Package> all() {
        return this.packageRepository.getPackages();
    }

    /**
     * Create a new package, the request format is as follows:
     *
     * <code><pre>
     * {
     *     "name": "My Test Package!",
     *     "description": "Amazing package",
     *     "products": [
     *         "VqKb4tyj9V6i",
     *         "PKM5pGAh9yGm"
     *     ]
     * }
     * </pre></code>
     *
     * @param request details of the package to create
     * @return the package we created or 400 if there was an invalid product passed
     */
    @RequestMapping(value = "/package", method = RequestMethod.POST)
    public ResponseEntity createPackage(@RequestBody PackageRequest request) {
        Package p = new Package();
        p.setName(request.getName());
        p.setDescription(request.getDescription());

        // loop over all the products given to us and add it to the package we just made
        for (String productId : request.getProducts()) {
            Product product = this.productRepository.getProductById(productId);

            if (product == null) {
                // we got passed an unknown product id, throw a 400
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            // add this product to the package
            p.getProducts().add(product);
        }

        // successfully built a package from the request! lets add it to our store
        this.packageRepository.addPackage(p);

        return ResponseEntity.ok(p);
    }

    /**
     * Get a package by UUID
     *
     * @param uuid uuid of the package to grab
     * @return the package or 404 if not found
     */
    @RequestMapping(value = "/package/{id}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> getPackage(@PathVariable("id") UUID uuid,
                                                          @RequestParam(value = "currency", defaultValue = "USD")
                                                                      String currency) {
        currency = currency.toUpperCase();

        if (!currency.equals("USD") && !this.exchangeRateRepository.getRates().containsKey(currency)) {
            // we don't know about this currency so throw a 400
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // multiplier defaults to 1 for USD
        final BigDecimal multiplier = this.exchangeRateRepository.getRates().getOrDefault(currency, BigDecimal.ONE);

        // get the package the user requested
        final Package p = this.packageRepository.getPackage(uuid);

        if (p == null) {
            // throw a 404 if we couldn't find the package
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // sum all products to get the total cost of this item in USD
        BigDecimal total = BigDecimal.valueOf(p.getProducts().stream().mapToInt(Product::getPrice).sum());

        // convert it to the requested currency
        BigDecimal price = total.multiply(multiplier);

        // convert our package to a hash map so we can easily add arbitrary key, value pairs
        HashMap<String, Object> res = new HashMap<String, Object>(BeanMap.create(p));

        // update price to the converted price & add currency we've converted to
        res.put("price", price.setScale(2, BigDecimal.ROUND_HALF_UP));
        res.put("currency", currency.toUpperCase());

        HashSet<HashMap> products = new HashSet<>();

        for (Product product : p.getProducts()) {
            // convert each product to a hash map
            HashMap<String, Object> productMap = new HashMap<String, Object>(BeanMap.create(product));

            // convert product price to the requested currency and update hash map
            productMap.put("price", BigDecimal.valueOf(product.getPrice()).multiply(multiplier)
                    .setScale(2, BigDecimal.ROUND_HALF_UP));

            products.add(productMap);
        }

        res.put("products", products);

        return ResponseEntity.ok(res);
    }

    /**
     * Update a package by UUID. All parameters are the same as {@link PackageController#createPackage} but they're
     * all optional.
     *
     * @param uuid    uuid of the package to update
     * @param request info to update the package with
     * @return the package with updated info
     */
    @RequestMapping(value = "/package/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Package> updatePackage(@PathVariable("id") UUID uuid,
                                                 @RequestBody(required = false) PackageRequest request) {
        Package p = this.packageRepository.getPackage(uuid);

        if (p == null) {
            // couldn't find the given uuid, throw a 404
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (request.getName() != null) {
            p.setName(request.getName());
        }

        if (request.getDescription() != null) {
            p.setDescription(request.getDescription());
        }

        if (request.getProducts() != null && !request.getProducts().isEmpty()) {
            // remove all products we already know about
            p.getProducts().clear();

            // loop over all the products given to us and add it to the package we just made
            for (String productId : request.getProducts()) {
                Product product = this.productRepository.getProductById(productId);

                // if we get passed an unknown product id, we'll ignore it since we're in the middle of updating
                if (product != null) {
                    p.getProducts().add(product);
                }
            }
        }

        return ResponseEntity.ok(p);
    }

    /**
     * Delete a package by UUID
     *
     * @param uuid uuid of the package to delete
     * @return 200 or 404 if not found
     */
    @RequestMapping(value = "/package/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePackage(@PathVariable("id") UUID uuid) {
        if (this.packageRepository.hasPackage(uuid)) {
            this.packageRepository.deletePackage(uuid);
            return ResponseEntity.ok("200");
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
