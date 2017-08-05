package uk.jordandoyle.packageservice.repository;

import org.springframework.stereotype.Repository;
import uk.jordandoyle.packageservice.domain.Package;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Datastore for packages. This is currently only backed by a HashMap and no persistent datastore.
 *
 * TODO: this should be persisted to DynamoDB or something and then this class repurposed as a second level cache.
 */
@Repository
public class PackageRepository {
    /**
     * All the packages we own
     */
    private final Map<UUID, Package> packages = new ConcurrentHashMap<>();

    /**
     * Adds a new package to our datastore.
     *
     * @param p package to add
     */
    public void addPackage(Package p) {
        this.packages.put(p.getUuid(), p);
    }

    /**
     * Check if we have a package by id.
     *
     * @param uuid uuid to check exists
     * @return true, if we have the given package
     */
    public boolean hasPackage(UUID uuid) {
        return this.packages.containsKey(uuid);
    }

    /**
     * Delete a package from our datastore.
     *
     * @param uuid uuid of the package to remove
     */
    public void deletePackage(UUID uuid) {
        this.packages.remove(uuid);
    }

    /**
     * Get a package from our datastore.
     *
     * @param uuid uuid of the package to get
     */
    public Package getPackage(UUID uuid) {
        return this.packages.get(uuid);
    }

    /**
     * Get all packages from our datastore. This method returns an unmodifiable collection. Use the other method
     * provided by this class to interact with the collection.
     */
    public Collection<Package> getPackages() {
        return Collections.unmodifiableCollection(this.packages.values());
    }
}
