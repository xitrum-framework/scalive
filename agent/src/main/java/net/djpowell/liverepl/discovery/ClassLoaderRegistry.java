package net.djpowell.liverepl.discovery;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClassLoaderDiscovery implementations must register all ClassLoaders with this registry to give them an
 * identifier which can be used to subsequently find the ClassLoader that they user asked for.
 *
 */
public class ClassLoaderRegistry {

    private final AtomicInteger clIdGenerator = new AtomicInteger(0);
    private final Map<ClassLoader, String> clIdMap = new WeakHashMap<ClassLoader, String>();

    public String registerClassLoader(ClassLoader classLoader) {
        String id;
        synchronized (clIdMap) {
            id = clIdMap.get(classLoader);
            if (id == null) {
                id = String.valueOf(clIdGenerator.getAndIncrement());
                clIdMap.put(classLoader, id);
            }
        }
        return id;
    }

}
