package scalive;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URLClassLoader;

public class Server {
    private static final String DEFAULT_SCALA_VERSION = "2.10.3";

    public static void serve(Socket client, String jarpath) throws Exception {
        InputStream  in  = client.getInputStream();
        OutputStream out = client.getOutputStream();

        InputStream oldIn  = System.in;
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;

        System.setIn(in);
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(out));

        try {
            URLClassLoader cl = (URLClassLoader) Server.class.getClassLoader();
            addJarsToClassLoader(cl, jarpath);

            Class<?> repl = Class.forName("scalive.Repl");
            Method method = repl.getMethod("run", URLClassLoader.class, InputStream.class, OutputStream.class);
            method.invoke(null, cl, in, out);
        } finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
            System.setErr(oldErr);
            System.out.println("[Scalive] REPL closed");
            client.close();
        }
    }

    private static void addJarsToClassLoader(URLClassLoader cl, String jarpath) throws Exception {
        // Try scala-library first
        addJarToClassLoader(cl, jarpath + "/" + DEFAULT_SCALA_VERSION, "scala-library", "scala.AnyVal");

        // So that we can get the actual Scala version being used
        String version = getScalaVersion();

        addJarToClassLoader(cl, jarpath + "/" + version, "scala-compiler", "scala.tools.nsc.interpreter.ILoop");
        addJarToClassLoader(cl, jarpath + "/" + version, "scala-reflect",  "scala.reflect.runtime.JavaUniverse");

        addJarToClassLoader(cl, jarpath, "scalive-repl", "scalive.Repl");
    }

    private static void addJarToClassLoader(
        URLClassLoader cl, String jarpath, String jarbase, String representativeClass
    ) throws Exception {
        try {
            Class.forName(representativeClass);
        } catch (ClassNotFoundException e) {
            System.out.println("[Scalive] Load " + jarbase);
            Classpath.findAndAddJar(cl, jarpath, jarbase);
        }
    }

    private static String getScalaVersion() throws Exception {
        Class<?> k = Class.forName("scala.util.Properties");
        Method   m = k.getDeclaredMethod("versionNumberString");
        return (String) m.invoke(k);
    }
}
