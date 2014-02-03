package net.djpowell.liverepl.discovery;

import java.util.Collection;

/**
 * SPI Interface for providing implementations to discover ClassLoaders.
 */
public interface ClassLoaderDiscovery {
    /**
     * Return information about the available ClassLoaders.
     * Implementations must register each ClassLoader with the application's ClassLoaderRegistry,
     * and use the id returned by the registry in the ClassLoaderInfo instances returned.
     */
    Collection<ClassLoaderInfo> listClassLoaders();
    String discoveryName();
}
