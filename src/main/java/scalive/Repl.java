package scalive;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import scala.Option;
import scala.tools.nsc.interpreter.ILoop;
import scala.tools.nsc.Settings;

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
        Settings settings = new Settings();

        // http://www.scala-lang.org/old/node/8002
        settings.classpath().value_$eq(classpath);

        // Without this class loader setting, the REPL and the target process will
        // see different instances of a static variable of the same class!
        // http://stackoverflow.com/questions/5950025/multiple-instances-of-static-variables
        settings.explicitParentLoader_$eq(Option.apply(cl));

        ILoop repl = new ILoop(
            new BufferedReader(new InputStreamReader(in)),
            new PrintWriter(out)
        );

        // This call does not return until the stream (connection) is closed
        repl.process(settings);
    }
}
