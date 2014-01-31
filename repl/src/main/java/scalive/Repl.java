package scalive;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import scala.tools.nsc.interpreter.ILoop;
import scala.tools.nsc.Settings;

public class Repl {
  public static void run(String jarpath, InputStream in, OutputStream out) {
    // Without the below settings, there will be error:
    // Failed to initialize compiler: object scala.runtime in compiler mirror not found.
    // ** Note that as of 2.8 scala does not assume use of the java classpath.
    // ** For the old behavior pass -usejavacp to scala, or if using a Settings
    // ** object programatically, settings.usejavacp.value = true.
    //
    // http://stackoverflow.com/questions/18150961/scala-runtime-in-compiler-mirror-not-found-but-working-when-started-with-xboo
    Settings settings = new Settings();
    settings.classpath().value_$eq(jarpath + "/*");

    ILoop repl = new ILoop(
      new BufferedReader(new InputStreamReader(in)),
      new PrintWriter(out)
    );

    // This call does not return until the stream (connection) is closed
    repl.process(settings);
  }
}
