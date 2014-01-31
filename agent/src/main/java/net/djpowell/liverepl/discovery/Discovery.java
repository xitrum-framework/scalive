package net.djpowell.liverepl.discovery;

import net.djpowell.liverepl.discovery.impl.SystemDiscovery;
import net.djpowell.liverepl.discovery.impl.ThreadDiscovery;
import net.djpowell.liverepl.discovery.impl.TomcatDiscovery;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * API for obtaining a list of ClassLoaders by using available implementations of ClassLoaderDiscovery
 */
public class Discovery implements ClassLoaderDiscovery {

    private final List<ClassLoaderDiscovery> impls = new ArrayList<ClassLoaderDiscovery>();

    public Discovery() {
        ClassLoaderRegistry registry=new ClassLoaderRegistry();
        impls.add(new SystemDiscovery(registry));
        impls.add(new TomcatDiscovery(registry));
        impls.add(new ThreadDiscovery(registry));
    }

    public Collection<ClassLoaderInfo> listClassLoaders() {
        List<ClassLoaderInfo> ret = new ArrayList<ClassLoaderInfo>();
        for (ClassLoaderDiscovery discovery : impls) {
            ret.addAll(discovery.listClassLoaders());
        }
        return ret;
    }

    public String discoveryName() {
        return "All ClassLoaders";
    }

    public void dumpList(PrintStream out) {
        for (ClassLoaderDiscovery discovery : impls) {
            Collection<ClassLoaderInfo> clis = discovery.listClassLoaders();
            if (!clis.isEmpty()) {
                out.println();
                out.println(discovery.discoveryName() + ":");
                out.println();
                out.println(ClassLoaderInfo.header);
                for (ClassLoaderInfo cli : clis) {
                    out.println(cli.toString());
                }
            }
        }
    }

    public ClassLoaderInfo findClassLoader(String clId) {
        for (ClassLoaderInfo cli : listClassLoaders()) {
            if (cli.id.equals(clId)) {
                return cli;
            }
        }
        return null;
    }

}
