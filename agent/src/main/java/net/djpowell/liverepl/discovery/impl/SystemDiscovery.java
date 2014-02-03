package net.djpowell.liverepl.discovery.impl;

import net.djpowell.liverepl.discovery.ClassLoaderDiscovery;
import net.djpowell.liverepl.discovery.ClassLoaderInfo;
import net.djpowell.liverepl.discovery.ClassLoaderRegistry;

import java.util.Collection;
import java.util.Collections;

/**
 * Simple implementation of ClassLoaderDiscovery for returning the SystemClassLoader.
 * This implementation should be registered first with Discovery to ensure that the
 * well-known SystemClassLoader gets assigned 0 as its id. 
 */
public class SystemDiscovery implements ClassLoaderDiscovery {

    private final String id;

    public SystemDiscovery(ClassLoaderRegistry registry) {
        id = registry.registerClassLoader(ClassLoader.getSystemClassLoader());
    }

    public Collection<ClassLoaderInfo> listClassLoaders() {
        ClassLoaderInfo cli = new ClassLoaderInfo(id, ClassLoader.getSystemClassLoader(), "<system>");
        return Collections.singletonList(cli);
    }

    public String discoveryName() {
        return "System Class Loader";
    }

}
