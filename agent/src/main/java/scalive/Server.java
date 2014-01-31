package scalive;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

import net.djpowell.liverepl.discovery.ClassLoaderInfo;
import net.djpowell.liverepl.discovery.Discovery;

public class Server {
    public static void serve(Socket client, String jarpath, String clId) throws Exception {
        InputStream  in  = client.getInputStream();
        OutputStream out = client.getOutputStream();

        InputStream oldIn  = System.in;
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;

        System.setIn(in);
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(out));

        ClassLoader oldCl = Thread.currentThread().getContextClassLoader();

        try {
            ClassLoader cl = createClassloader(jarpath, clId);
            if (cl == null) return;

            Thread.currentThread().setContextClassLoader(cl);

            Class<?> repl = Class.forName("scalive.Repl", true, cl);
            Method method = repl.getMethod("run", String.class, InputStream.class, OutputStream.class);
            method.invoke(null, jarpath, in, out);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCl);
            System.setIn(oldIn);
            System.setOut(oldOut);
            System.setErr(oldErr);
            System.out.println("[Scalive] REPL closed");
            client.close();
        }
    }

    private static ClassLoader createClassloader(String jarpath, String clId) throws Exception {
        Discovery d = new Discovery();

        if (clId == null) {
            if (d.listClassLoaders().size() > 1) {
                d.dumpList(System.out);
                return null;
            } else {
                clId = "0";
            }
        }

        ClassLoaderInfo cli = d.findClassLoader(clId);
        if (cli == null) {
            System.out.println("[Scalive] Could not find class loader: " + clId);
            return null;
        }

        ClassLoader cl = cli.getClassLoader();

        URL[] urls = new URL[] {
            new File(Classpath.findJar(jarpath, "scala-library")).toURI().toURL(),
            new File(Classpath.findJar(jarpath, "scala-compiler")).toURI().toURL(),
            new File(Classpath.findJar(jarpath, "scala-reflect")).toURI().toURL(),
            new File(Classpath.findJar(jarpath, "scalive-repl")).toURI().toURL()
        };
        URLClassLoader withScala = new URLClassLoader(urls, cl);
        return withScala;
    }
}
