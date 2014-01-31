package net.djpowell.liverepl.discovery;

import java.lang.ref.WeakReference;

public class ClassLoaderInfo implements Comparable<Object> {

    public final String id;
    private final WeakReference<ClassLoader> classLoader;
    public final String info;

    public ClassLoaderInfo(String id, ClassLoader classLoader, String info) {
        this.id = id;
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.info = info;
    }

    public ClassLoader getClassLoader() {
        return classLoader.get();
    }

    public String getClassLoaderName() {
        ClassLoader cl = classLoader.get();
        if (cl == null) {
            return "<null>";
        } else {
            return cl.getClass().getSimpleName();
        }
    }

    public static final String header = String.format("#%-3s %-20s : %s", "Id", "ClassLoader", "Info");

    public String toString() {
        return String.format("#%-3s %-20s : %s", id, getClassLoaderName(), info);
    }

    public int compareTo(Object o) {
        ClassLoaderInfo cli = (ClassLoaderInfo)o;
        return id.compareTo(cli.id);
    }
}
