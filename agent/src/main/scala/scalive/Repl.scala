package scalive

import java.io.{
  BufferedReader,
  InputStream, InputStreamReader,
  OutputStream, PrintStream, PrintWriter
}

import scala.tools.nsc.interpreter.ILoop
import scala.tools.nsc.Settings

class Repl(in: InputStream, out: OutputStream) {
  private val settings = new Settings
  settings.usejavacp.value = true

  private val repl = new ILoop(
    new BufferedReader(new InputStreamReader(in)),
    new PrintWriter(out)
  )

  def start() {
    // Java
    System.setIn(in)
    System.setOut(new PrintStream(out))
    System.setErr(new PrintStream(out))

    // Scala
    Console.setIn(in)
    Console.setOut(out)
    Console.setErr(out)

    // This call does not return until the stream (connection) is closed
    repl.process(settings)
  }

  def stop() {
    repl.closeInterpreter()
  }
}
