package net.djpowell.liverepl.discovery.impl;

import net.djpowell.liverepl.discovery.ClassLoaderDiscovery;
import net.djpowell.liverepl.discovery.ClassLoaderInfo;
import net.djpowell.liverepl.discovery.ClassLoaderRegistry;

import java.util.*;

/**
 * Implementation of ClassLoaderDiscovery that makes available the Thread Context ClassLoaders
 * associated with currently running threads. 
 */
public class ThreadDiscovery implements ClassLoaderDiscovery {

    private final ClassLoaderRegistry registry;

    public ThreadDiscovery(ClassLoaderRegistry registry) {
        this.registry = registry;
    }

    public Collection<ClassLoaderInfo> listClassLoaders() {
        Collection<ClassLoaderInfo> ret = new ArrayList<ClassLoaderInfo>();
        ClassLoader systemCl = ClassLoader.getSystemClassLoader();
        Collection<Thread> threads = new HashSet<Thread>(Thread.getAllStackTraces().keySet());
        Map<ClassLoader, String> classLoaders = new HashMap<ClassLoader, String>();
        for (Thread thread : threads) {
            ClassLoader classLoader = thread.getContextClassLoader();
            if (classLoader == null) continue;
            if (classLoader == systemCl) continue;
            classLoaders.put(classLoader, thread.getName() + " #" + thread.getId() + " [" + thread.getThreadGroup().getName() + "]");
        }
        TreeSet<Map.Entry<ClassLoader, String>> entries = new TreeSet<Map.Entry<ClassLoader, String>>(
                new Comparator<Map.Entry<ClassLoader, String>>() {
                    public int compare(Map.Entry<ClassLoader, String> o1, Map.Entry<ClassLoader, String> o2) {
                        return o1.getValue().compareTo(o2.getValue());
                    }
                });
        entries.addAll(classLoaders.entrySet());
        for (Map.Entry<ClassLoader, String> entry : entries) {
            ClassLoader classLoader = entry.getKey();
            String threadName = entry.getValue();
            String id = registry.registerClassLoader(classLoader);
            ClassLoaderInfo cli = new ClassLoaderInfo(id, classLoader, threadName);
            ret.add(cli);
        }
        return ret;
    }

    public String discoveryName() {
        return "Thread Context Class Loaders";
    }
}
