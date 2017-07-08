package uk.jordandoyle.packageservice.repository;

import org.springframework.stereotype.Repository;
import uk.jordandoyle.packageservice.domain.Package;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
    private final Map<UUID, Package> PACKAGES = new HashMap<>();

    /**
     * Adds a new package to our datastore.
     *
     * @param p package to add
     */
    public void addPackage(Package p) {
        this.PACKAGES.put(p.getUuid(), p);
    }

    /**
     * Check if we have a package by id.
     *
     * @param uuid uuid to check exists
     * @return true, if we have the given package
     */
    public boolean hasPackage(UUID uuid) {
        return this.PACKAGES.containsKey(uuid);
    }

    /**
     * Delete a package from our datastore.
     *
     * @param uuid uuid of the package to remove
     */
    public void deletePackage(UUID uuid) {
        this.PACKAGES.remove(uuid);
    }

    /**
     * Get a package from our datastore.
     *
     * @param uuid uuid of the package to get
     */
    public Package getPackage(UUID uuid) {
        return this.PACKAGES.get(uuid);
    }

    /**
     * Get all packages from our datastore
     */
    public Set<Package> getPackages() {
        return new HashSet<>(this.PACKAGES.values());
    }
}
