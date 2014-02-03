package scalive;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import net.djpowell.liverepl.discovery.ClassLoaderInfo;
import net.djpowell.liverepl.discovery.Discovery;

public class Server {
    // Load this Scala version if Scala has not been loaded in the target process
    private static final String DEFAULT_SCALA_VERSION = "2.10.3";

    // Use only one instance so that we have a stable list of class loaders
    private static final Discovery DISCOVERY = new Discovery();

    public static void serve(Socket client, String jarpath, String clId) throws Exception {
        InputStream  in  = client.getInputStream();
        OutputStream out = client.getOutputStream();

        InputStream oldIn  = System.in;
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;

        System.setIn(in);
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(out));

        try {
            ClassLoader parentCl = getClassloader(clId);
            if (parentCl == null) return;

            URLClassLoader cl        = createClassLoaderWithReplJars(jarpath, parentCl);
            Class<?>       repl      = Class.forName("scalive.Repl", true, cl);
            Method         method    = repl.getMethod("run", String.class, InputStream.class, OutputStream.class);
            String         classpath = getClasspathForRepl(cl, parentCl);
            method.invoke(null, classpath, in, out);
        } finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
            System.setErr(oldErr);
            System.out.println("[Scalive] REPL closed");
            client.close();
        }
    }

    //--------------------------------------------------------------------------

    /**
     * @param clId null means no class loader ID is specified; in this case,
     * when there's only one class loader, it will be returned
     *
     * @return null if class loader not found or there are multiple class loaders
     * but clId is not specified
     */
    private static ClassLoader getClassloader(String clId) {
        if (clId == null) {
            if (DISCOVERY.listClassLoaders().size() > 1) {
                DISCOVERY.dumpList(System.out);
                return null;
            } else {
                clId = "0";
            }
        }

        ClassLoaderInfo cli = DISCOVERY.findClassLoader(clId);
        if (cli == null) {
            System.out.println("[Scalive] Could not find class loader: " + clId);
            return null;
        }

        return cli.getClassLoader();
    }

    //--------------------------------------------------------------------------

    private static URLClassLoader createClassLoaderWithReplJars(String jarpath, ClassLoader parentCl) throws Exception {
        URLClassLoader cl = new URLClassLoader(new URL[] {}, parentCl);

        // Try scala-library first
        Classpath.addJarToURLClassLoader(cl, jarpath + "/" + DEFAULT_SCALA_VERSION, "scala-library", "scala.AnyVal");

        // So that we can get the actual Scala version being used
        String version = getScalaVersion(cl);

        Classpath.addJarToURLClassLoader(cl, jarpath + "/" + version, "scala-reflect",  "scala.reflect.runtime.JavaUniverse");
        Classpath.addJarToURLClassLoader(cl, jarpath + "/" + version, "scala-compiler", "scala.tools.nsc.interpreter.ILoop");

        Classpath.addJarToURLClassLoader(cl, jarpath, "scalive-repl", "scalive.Repl");

        return cl;
    }

    private static String getScalaVersion(ClassLoader cl) throws Exception {
        Class<?> k = Class.forName("scala.util.Properties", true, cl);
        Method   m = k.getDeclaredMethod("versionNumberString");
        return (String) m.invoke(k);
    }

    //--------------------------------------------------------------------------

    /** @return The classpath that should be set to the REPL
     * @throws Exception */
    private static String getClasspathForRepl(URLClassLoader withReplJars, ClassLoader parentCl) throws Exception {
        String fromParent = null;
        String fromRepl   = getURLClasspath(withReplJars);

        if (parentCl.getClass().getName().equals("sbt.classpath.ClasspathFilter")) {
            fromParent = sbtGetClasspathFromClasspathFilter(withReplJars, parentCl);
        } else if (parentCl instanceof URLClassLoader) {
            fromParent = getURLClasspath((URLClassLoader) parentCl);
        }

        return (fromParent == null) ? fromRepl : fromParent + File.pathSeparator + fromRepl;
    }

    // http://stackoverflow.com/questions/4121567/embedded-scala-repl-inherits-parent-classpath
    private static String getURLClasspath(URLClassLoader cl) {
        URL[] urls = cl.getURLs();
        StringBuffer buf = new StringBuffer();
        for (URL url: urls) {
            if (buf.length() > 0) buf.append(File.pathSeparator);
            buf.append(url);
        }
        return buf.toString();
    }

    //--------------------------------------------------------------------------

    // http://www.scala-sbt.org/release/api/index.html#sbt.classpath.ClasspathFilter
    private static String sbtGetClasspathFromClasspathFilter(URLClassLoader withReplJars, ClassLoader parentCl) throws Exception {
        ClassLoader mainClassLoader      = sbtFindMainClassLoader();
        Class<?>    classpathFilterClass = Class.forName("sbt.classpath.ClasspathFilter", true, mainClassLoader);
        Field       classpathField       = classpathFilterClass.getDeclaredField("classpath");
        classpathField.setAccessible(true);

        Object   set      = classpathField.get(parentCl);
        Class<?> setClass = Class.forName("scala.collection.TraversableOnce", true, withReplJars);
        Method   mkString = setClass.getDeclaredMethod("mkString", String.class);
        return (String) mkString.invoke(set, File.pathSeparator);
    }

    private static ClassLoader sbtFindMainClassLoader() throws Exception {
        // System Class Loader:
        //
        // #Id  ClassLoader          Info
        // 0   AppClassLoader        <system>
        //
        // Thread Context Class Loaders:
        //
        // #Id  ClassLoader          Info
        // 1   URLClassLoader        main #1 [main]  <--- Take out this one
        // 2   ClasspathFilter       xitrum-akka.actor.default-dispatcher-2 #31 [run-main-group-0]

        Collection<ClassLoaderInfo> clis = DISCOVERY.listClassLoaders();
        for (ClassLoaderInfo cli: clis) {
            ClassLoader cl   = cli.getClassLoader();
            String      name = cli.getClassLoaderName();

            // Can't use info because it can be "process reaper #8 [system]" etc.
            boolean mainClassLoader = cl instanceof URLClassLoader && "URLClassLoader".equals(name);

            if (mainClassLoader) return cl;
        }

        throw new Exception("[Scalive] SBT main class loader not found");
    }
}
