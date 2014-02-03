package scalive;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import scala.tools.nsc.interpreter.ILoop;
import scala.tools.nsc.Settings;

// This must be split to a separate JAR (scalive-repl.jar), different from
// the JAR file that contains the agent (scalive-agent.jar). Otherwise the agent
// can't load this Repl class.
public class Repl {
    public static void run(String classpath, InputStream in, OutputStream out) {
        // Without the below settings, there will be error:
        // Failed to initialize compiler: object scala.runtime in compiler mirror not found.
        // ** Note that as of 2.8 scala does not assume use of the java classpath.
        // ** For the old behavior pass -usejavacp to scala, or if using a Settings
        // ** object programatically, settings.usejavacp.value = true.
        //
        // "usejavacp" (System.getProperty("scala.class.path")) is not enough when
        // there are multiple class loaders (and even when there's only one class
        // loader but the "scala.class.path" system property does not contain Scala JARs
        //
        // http://stackoverflow.com/questions/18150961/scala-runtime-in-compiler-mirror-not-found-but-working-when-started-with-xboo
        Settings settings = new Settings();
        settings.classpath().value_$eq(classpath);

        ILoop repl = new ILoop(
            new BufferedReader(new InputStreamReader(in)),
            new PrintWriter(out)
        );

        // This call does not return until the stream (connection) is closed
        repl.process(settings);
    }
}
