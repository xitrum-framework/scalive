package scalive.server;

import scala.Console;
import scala.Option;
import scala.runtime.AbstractFunction0;
import scala.tools.nsc.Settings;

import scalive.Classpath;
import scalive.Log;
import scalive.Net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URLClassLoader;

class Repl {
    /** Creates a REPL and wire IO streams of the socket to it. */
    static ILoopWithCompletion run(
            final Socket socket, URLClassLoader cl, final Runnable socketCleaner
    ) throws IOException {
        final InputStream  in  = socket.getInputStream();
        final OutputStream out = socket.getOutputStream();

        final ILoopWithCompletion iloop    = new ILoopWithCompletion(in, out);
        final Settings            settings = getSettings(cl);

        Net.throwSocketTimeoutExceptionForLongInactivity(socket);
        new Thread(Repl.class.getName() + "-iloop") {
            @Override
            public void run() {
                overrideScalaConsole(in, out, new Runnable() {
                    @Override
                    public void run() {
                        // This call does not return until socket is closed,
                        // or repl has been closed by the client using ":q"
                        try {
                            iloop.process(settings);
                        } catch (Exception e) {
                            // See throwSocketTimeoutExceptionForLongInactivity above;
                            // just let this thread ends
                        }
                    }
                });

                // This code should be put outside overrideScalaConsole above
                // so that the output is not redirected to the client,
                // in case repl has been closed by the client using ":q"
                socketCleaner.run();
                Log.log("REPL closed");
            }
        }.start();

        return iloop;
    }

    private static Settings getSettings(URLClassLoader cl) {
        // Without the below settings, there will be error:
        // Failed to initialize compiler: object scala.runtime in compiler mirror not found.
        // ** Note that as of 2.8 scala does not assume use of the java classpath.
        // ** For the old behavior pass -usejavacp to scala, or if using a Settings
        // ** object programatically, settings.usejavacp.value = true.
        //
        // "usejavacp" (System.getProperty("java.class.path")) is not enough when
        // there are multiple class loaders (and even when there's only one class
        // loader but the "java.class.path" system property does not contain Scala JARs
        //
        // http://stackoverflow.com/questions/18150961/scala-runtime-in-compiler-mirror-not-found-but-working-when-started-with-xboo
        Settings settings = new Settings();

        // http://www.scala-lang.org/old/node/8002
        String classpath = Classpath.getClasspath(cl);
        settings.classpath().value_$eq(classpath);

        // Without this class loader setting, the REPL and the target process will
        // see different instances of a static variable of the same class!
        // http://stackoverflow.com/questions/5950025/multiple-instances-of-static-variables
        settings.explicitParentLoader_$eq(Option.apply((ClassLoader) cl));

        return settings;
    }

    // https://github.com/xitrum-framework/scalive/issues/11
    // http://stackoverflow.com/questions/25623779/implementing-a-scala-function-in-java
    private static void overrideScalaConsole(final InputStream in, final OutputStream out, final Runnable runnable) {
        Console.withIn(in, new AbstractFunction0<Object>() {
            @Override
            public Object apply() {
                Console.withOut(out, new AbstractFunction0<Object>() {
                    @Override
                    public Object apply() {
                        Console.withErr(out, new AbstractFunction0<Object>() {
                            @Override
                            public Object apply() {
                                withIO(in, out, runnable);
                                return null;
                            }
                        });
                        return null;
                    }
                });
                return null;
            }
        });
    }

    private static void withIO(InputStream in, OutputStream out, Runnable runnable) {
        InputStream originalIn  = System.in;
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        try {
            System.setIn(in);
            System.setOut(new PrintStream(out));
            System.setErr(new PrintStream(out));

            runnable.run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
