package scalive;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;

import scala.tools.nsc.interpreter.ILoop;
import scala.tools.nsc.Settings;

// This must be split to a separate JAR file (scalive-repl.jar), different from
// the JAR file that contains the agent (scalive-agent.jar). Otherwise the agent
// can't load this Repl class.
public class Repl {
  public static void run(URLClassLoader cl, InputStream in, OutputStream out) {
    // Without the below settings, there will be error:
    // Failed to initialize compiler: object scala.runtime in compiler mirror not found.
    // ** Note that as of 2.8 scala does not assume use of the java classpath.
    // ** For the old behavior pass -usejavacp to scala, or if using a Settings
    // ** object programatically, settings.usejavacp.value = true.
    //
    // "usejavacp" is not enough because the current classloader has been modified.
    //
    // http://stackoverflow.com/questions/18150961/scala-runtime-in-compiler-mirror-not-found-but-working-when-started-with-xboo
    Settings settings = new Settings();
    settings.classpath().value_$eq(getClasspath(cl));

    ILoop repl = new ILoop(
        new BufferedReader(new InputStreamReader(in)),
        new PrintWriter(out)
    );

    // This call does not return until the stream (connection) is closed
    repl.process(settings);
  }

  // http://stackoverflow.com/questions/4121567/embedded-scala-repl-inherits-parent-classpath
  private static String getClasspath(URLClassLoader cl) {
      URL[] urls = cl.getURLs();
      StringBuffer buf = new StringBuffer();
      for (URL url: urls) {
          if (buf.length() > 0) buf.append(File.pathSeparator);
          buf.append(url);
      }
      return buf.toString();
  }
}
