package scalive;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URLClassLoader;

public class Server {
    public static void serve(Socket socket, String[] jarSearchDirs) throws Exception {
        // Create a REPL console and wire IO streams of the socket to it

        InputStream  in  = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        InputStream oldIn  = System.in;
        PrintStream oldOut = System.out;
        PrintStream oldErr = System.err;

        System.setIn(in);
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(out));

        try {
            URLClassLoader cl = (URLClassLoader) ClassLoader.getSystemClassLoader();
            loadDependencyJars(cl, jarSearchDirs);

            String classpath = Classpath.getClasspath(cl);
            Repl.run(cl, classpath, in, out);
        } finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
            System.setErr(oldErr);
            System.out.println("[Scalive] REPL closed");
            socket.close();
        }
    }

    private static void loadDependencyJars(URLClassLoader cl, String[] jarSearchDirs) throws Exception {
        // Try scala-library first
        Classpath.findAndAddJar(cl, "scala.AnyVal", jarSearchDirs, "scala-library");

        // So that we can get the actual Scala version being used
        String version = Classpath.getScalaVersion(cl);

        Classpath.findAndAddJar(cl, "scala.reflect.runtime.JavaUniverse", jarSearchDirs, "scala-reflect-"  + version);
        Classpath.findAndAddJar(cl, "scala.tools.nsc.interpreter.ILoop",  jarSearchDirs, "scala-compiler-" + version);

        Classpath.findAndAddJar(cl, "scalive.Repl", jarSearchDirs, "scalive");
    }
}
