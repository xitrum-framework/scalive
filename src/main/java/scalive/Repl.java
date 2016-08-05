package scalive;

import scala.Console;
import scala.Option;
import scala.runtime.AbstractFunction0;
import scala.tools.nsc.Settings;
import scala.tools.nsc.interpreter.ILoop;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class Repl {
    public static void run(ClassLoader cl, String classpath, InputStream in, OutputStream out) {
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
        final Settings settings = new Settings();

        // http://www.scala-lang.org/old/node/8002
        settings.classpath().value_$eq(classpath);

        // Without this class loader setting, the REPL and the target process will
        // see different instances of a static variable of the same class!
        // http://stackoverflow.com/questions/5950025/multiple-instances-of-static-variables
        settings.explicitParentLoader_$eq(Option.apply(cl));

        final ILoop repl = new ILoop(
            new BufferedReader(new InputStreamReader(in)),
            new PrintWriter(out)
        );

        overrideScalaConsole(in, out, new Runnable() {
            @Override
            public void run() {
                repl.process(settings);
            }
        });
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
                                // This call does not return until the stream (connection) is closed
                                runnable.run();
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
}
