package scalive;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URLClassLoader;

public class Server {
    // Load this Scala version if Scala has not been loaded in the target process
    private static final String DEFAULT_SCALA_VERSION = "2.11.4";

    public static void serve(Socket client, String[] jarpaths) throws Exception {
        InputStream  in  = client.getInputStream();
        OutputStream out = client.getOutputStream();

        InputStream oldIn  = System.in;
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;

        System.setIn(in);
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(out));

        try {
            URLClassLoader cl = (URLClassLoader) ClassLoader.getSystemClassLoader();
            addJarsToURLClassLoader(cl, jarpaths);

            String   classpath = Classpath.getClasspath(cl);
            Class<?> repl      = Class.forName("scalive.Repl");
            Method   method    = repl.getMethod("run", ClassLoader.class, String.class, InputStream.class, OutputStream.class);
            method.invoke(null, cl, classpath, in, out);
        } finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
            System.setErr(oldErr);
            System.out.println("[Scalive] REPL closed");
            client.close();
        }
    }

    private static void addJarsToURLClassLoader(URLClassLoader cl, String[] jarpaths) throws Exception {
        // Try scala-library first
        Classpath.findAndAddJar(cl, jarpaths, "scala-library-" + DEFAULT_SCALA_VERSION, "scala.AnyVal");

        // So that we can get the actual Scala version being used
        String version = Classpath.getScalaVersion(cl);

        Classpath.findAndAddJar(cl, jarpaths, "scala-reflect-"  + version, "scala.reflect.runtime.JavaUniverse");
        Classpath.findAndAddJar(cl, jarpaths, "scala-compiler-" + version, "scala.tools.nsc.interpreter.ILoop");

        Classpath.findAndAddJar(cl, jarpaths, "scalive", "scalive.Repl");
    }
}
