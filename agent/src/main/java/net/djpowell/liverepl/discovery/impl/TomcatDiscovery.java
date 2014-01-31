package net.djpowell.liverepl.discovery.impl;

import net.djpowell.liverepl.discovery.ClassLoaderDiscovery;
import net.djpowell.liverepl.discovery.ClassLoaderInfo;
import net.djpowell.liverepl.discovery.ClassLoaderRegistry;
import net.djpowell.liverepl.discovery.Function;

import javax.management.ObjectName;
import java.util.Collection;

/**
 * Implementation of ClassLoaderDiscovery which uses JMX to obtain the ClassLoaders associated
 * with Tomcat web applications.
 */
public class TomcatDiscovery implements ClassLoaderDiscovery {

    private final JMXDiscovery jmxDiscovery;

    public TomcatDiscovery(ClassLoaderRegistry registry) {
        this.jmxDiscovery = new JMXDiscovery(registry, "Catalina:j2eeType=WebModule,*", "loader",
                new Function<ClassLoader, Object>() {
                    public ClassLoader invoke(Object obj) {
                        ClassLoader classLoader;
                        try {
                            classLoader = (ClassLoader) obj.getClass().getMethod("getClassLoader").invoke(obj);
                        } catch (RuntimeException e) {
                            throw e;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return classLoader;
                    }
                },
                new Function<String, ObjectName>() {
                    public String invoke(ObjectName arg) {
                        String name = arg.getKeyProperty("name");
                        int pos = name.lastIndexOf('/');
                        if (pos != -1) name = name.substring(pos);
                        return name;
                    }
                }, "Tomcat Web Applications");
    }

    public Collection<ClassLoaderInfo> listClassLoaders() {
        return jmxDiscovery.listClassLoaders();
    }

    public String discoveryName() {
        return jmxDiscovery.discoveryName();
    }
}
