package scalive

import java.io.{
  BufferedReader,
  InputStream, InputStreamReader,
  OutputStream, PrintStream, PrintWriter
}

import scala.tools.nsc.interpreter.ILoop
import scala.tools.nsc.Settings

class Repl(jarpath: String, in: InputStream, out: OutputStream) {
  def start() {
    // Java
    val oldIn  = System.in
    val oldOut = System.out
    val oldErr = System.err
    System.setIn(in)
    System.setOut(new PrintStream(out))
    System.setErr(new PrintStream(out))

    // Scala
    Console.setIn(in)
    Console.setOut(out)
    Console.setErr(out)

    // Without the below settings, there will be error:
    // Failed to initialize compiler: object scala.runtime in compiler mirror not found.
    // ** Note that as of 2.8 scala does not assume use of the java classpath.
    // ** For the old behavior pass -usejavacp to scala, or if using a Settings
    // ** object programatically, settings.usejavacp.value = true.
    //
    // http://stackoverflow.com/questions/18150961/scala-runtime-in-compiler-mirror-not-found-but-working-when-started-with-xboo
    val settings = new Settings
    settings.usejavacp.value = true
    settings.classpath.value = jarpath + "/*"

    val repl = new ILoop(
      new BufferedReader(new InputStreamReader(in)),
      new PrintWriter(out)
    )

    // This call does not return until the stream (connection) is closed
    repl.process(settings)

    System.setIn(oldIn)
    System.setOut(oldOut)
    System.setErr(oldErr)
    Console.setIn(oldIn)
    Console.setOut(oldOut)
    Console.setErr(oldErr)

    println("[Scalive] REPL closed")
  }
}
