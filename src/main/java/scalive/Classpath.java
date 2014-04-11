package scalive;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class Classpath {
    private static Method addURL = getAddURL();

    // http://stackoverflow.com/questions/8222976/why-urlclassloader-addurl-protected-in-java
    private static Method getAddURL() {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //--------------------------------------------------------------------------

    /**
     * @param jarpaths Directories to search for the JAR
     *
     * @param jarbase JAR file name prefix; Ex: "scalive-agent" will match "scalive-agent-xxx.jar"
     */
    public static String findJar(String[] jarpaths, String jarbase) throws Exception {
        for (String jarpath: jarpaths) {
            File   dir   = new File(jarpath);
            File[] files = dir.listFiles();

            for (int i = 0; i < files.length; i++) {
                File   file = files[i];
                String name = file.getName();
                if (file.isFile() && name.endsWith(".jar") && name.startsWith(jarbase))
                    return file.getPath();
            }
        }

        throw new Exception("Could not find " + jarbase + " in " + join(jarpaths, File.pathSeparator));
    }

    public static void addPath(URLClassLoader cl, String path) throws Exception {
        URL   url  = new File(path).toURI().toURL();
        URL[] urls = cl.getURLs();
        if (!Arrays.asList(urls).contains(url)) addURL.invoke(cl, url);
    }

    /** Combination of findJar and addPath. */
    public static void findAndAddJar(URLClassLoader cl, String[] jarpaths, String jarbase) throws Exception {
        String jar = findJar(jarpaths, jarbase);
        addPath(cl, jar);
    }

    /**
     * Similar to findAndAddJar without representativeClass, but only find and
     * add the JAR to classpath if the representativeClass has not been loaded.
     */
    public static void findAndAddJar(
        URLClassLoader cl, String[] jarpaths, String jarbase, String representativeClass
    ) throws Exception {
        try {
            Class.forName(representativeClass, true, cl);
        } catch (ClassNotFoundException e) {
            System.out.println("[Scalive] Load " + jarbase);
            findAndAddJar(cl, jarpaths, jarbase);
        }
    }

    // http://stackoverflow.com/questions/4121567/embedded-scala-repl-inherits-parent-classpath
    public static String getClasspath(URLClassLoader cl) {
        URL[] urls = cl.getURLs();
        return join(urls, File.pathSeparator);
    }

    public static String getScalaVersion(ClassLoader cl) throws Exception {
        Class<?> k = Class.forName("scala.util.Properties", true, cl);
        Method   m = k.getDeclaredMethod("versionNumberString");
        return (String) m.invoke(k);
    }

    //--------------------------------------------------------------------------

    private static String join(Object[] xs, String separator) {
        StringBuffer buf = new StringBuffer();
        for (Object x: xs) {
            if (buf.length() > 0) buf.append(separator);
            buf.append(x);
        }
        return buf.toString();
    }
}
