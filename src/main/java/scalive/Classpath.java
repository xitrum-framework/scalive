package scalive;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Classpath {
    private static final Method addURL = getAddURL();

    // http://stackoverflow.com/questions/8222976/why-urlclassloader-addurl-protected-in-java
    private static Method getAddURL() {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    //--------------------------------------------------------------------------

    /**
     * Finds the ".jar" file with the {@code jarPrefix} and the latest version.
     *
     * @param jarSearchDirs Directories to search for the JAR
     *
     * @param jarPrefix JAR file name prefix to search; example: "scalive-agent" will match "scalive-agent-xxx.jar"
     */
    public static String findJar(String[] jarSearchDirs, String jarPrefix) throws IllegalStateException {
        String maxBaseName = null;
        File   maxFile     = null;
        for (String jarSearchDir: jarSearchDirs) {
            File dir     = new File(jarSearchDir);
            File[] files = dir.listFiles();
            if (files == null) continue;

            for (File file : files) {
                String baseName = file.getName();
                if (file.isFile() && baseName.endsWith(".jar") && baseName.startsWith(jarPrefix)) {
                    if (maxBaseName == null || baseName.compareTo(maxBaseName) > 0) {
                        maxBaseName = baseName;
                        maxFile     = file;
                    }
                }
            }
        }

        if (maxFile == null)
            throw new IllegalStateException("Could not find " + jarPrefix + " in " + String.join(File.pathSeparator, jarSearchDirs));
        else
            return maxFile.getPath();
    }

    public static void addPath(
            URLClassLoader cl, String path
    ) throws MalformedURLException, InvocationTargetException, IllegalAccessException {
        URL   url  = new File(path).toURI().toURL();
        URL[] urls = cl.getURLs();
        if (!Arrays.asList(urls).contains(url)) addURL.invoke(cl, url);
    }

    /** Combination of {@link #findJar(String[], String)} and {@link #addPath(URLClassLoader, String)}. */
    public static void findAndAddJar(
            URLClassLoader cl, String[] jarSearchDirs, String jarPrefix
    ) throws IllegalAccessException, InvocationTargetException, MalformedURLException {
        String jar = findJar(jarSearchDirs, jarPrefix);
        addPath(cl, jar);
        Log.log("Load " + jar);
    }

    /**
     * Similar to {@link #findAndAddJar(URLClassLoader, String[], String)} without {@code representativeClass},
     * but only find and add the JAR to classpath if the representativeClass has not been loaded.
     */
    public static void findAndAddJar(
            URLClassLoader cl, String representativeClass, String[] jarSearchDirs, String jarPrefix
    ) throws IllegalAccessException, MalformedURLException, InvocationTargetException {
        try {
            Class.forName(representativeClass, true, cl);
        } catch (ClassNotFoundException e) {
            findAndAddJar(cl, jarSearchDirs, jarPrefix);
        }
    }

    // http://stackoverflow.com/questions/4121567/embedded-scala-repl-inherits-parent-classpath
    public static String getClasspath(URLClassLoader cl) {
        URL[] urls = cl.getURLs();
        return Arrays.stream(urls).map(Objects::toString).collect(Collectors.joining(File.pathSeparator));
    }

    public static String getScalaVersion(
            ClassLoader cl
    ) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> k = Class.forName("scala.util.Properties", true, cl);
        Method   m = k.getDeclaredMethod("versionNumberString");
        return (String) m.invoke(k);
    }
}
