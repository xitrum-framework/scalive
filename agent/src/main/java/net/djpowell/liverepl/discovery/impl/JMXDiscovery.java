package net.djpowell.liverepl.discovery.impl;

import net.djpowell.liverepl.discovery.ClassLoaderDiscovery;
import net.djpowell.liverepl.discovery.ClassLoaderInfo;
import net.djpowell.liverepl.discovery.ClassLoaderRegistry;
import net.djpowell.liverepl.discovery.Function;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * Helper class to return ClassLoaders using JMX
 */
public class JMXDiscovery implements ClassLoaderDiscovery {

    private final ClassLoaderRegistry registry;
    private final MBeanServer mbs;
    private final String searchName;
    private final String attributeName;
    private final Function<ClassLoader, Object> getClassLoader;
    private final Function<String, ObjectName> getInfo;
    private final String discoveryName;

    public JMXDiscovery(ClassLoaderRegistry registry, String searchName, String attributeName, Function<ClassLoader, Object> getClassLoader, Function<String, ObjectName> getInfo, String discoveryName) {
        this.registry = registry;
        this.mbs = ManagementFactory.getPlatformMBeanServer();
        this.searchName = searchName;
        this.attributeName = attributeName;
        this.getClassLoader = getClassLoader;
        this.getInfo = getInfo;
        this.discoveryName = discoveryName;
    }

    public Collection<ClassLoaderInfo> listClassLoaders() {
        Set<ClassLoaderInfo> ret = new TreeSet<ClassLoaderInfo>();
        Set<ObjectName> names; 
        try {
            names = mbs.queryNames(new ObjectName(searchName), null);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }

        for (ObjectName name : names) {
            Object obj;
            try {
                obj = mbs.getAttribute(name, attributeName);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ClassLoader classLoader = getClassLoader.invoke(obj);
            String id = registry.registerClassLoader(classLoader);
            String info = getInfo.invoke(name);
            ClassLoaderInfo cli = new ClassLoaderInfo(id, classLoader, info);
            ret.add(cli);
        }
        return ret;
    }

    public String discoveryName() {
        return discoveryName;
    }
}
