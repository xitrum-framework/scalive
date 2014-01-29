package scalive

import java.lang.instrument.Instrumentation
import java.net.ServerSocket

object Agent {
  /**
   * @param agentArgs <port> <classpath>; classpath is the directory that
   * contains scala-compiler.jar (for REPL) and its dependencies
   * (scala-library.jar and scala-reflect.jar)
   */
  def agentmain(agentArgs: String, inst: Instrumentation) {
    // Avoid using Scala methods, only use Java methods until the classpath is added
    val args      = agentArgs.split(" ")
    val classpath = args(1)
    addClasspath(classpath)

    val port = args(0).toInt
    println("Scalive REPL server loaded at port " + port)
    startTcpRepl(port)
  }

  /**
   * @param args <port>
   */
  def main(args: Array[String]) {
    val port = args(0).toInt
    startTcpRepl(port)
  }

  // http://www.scala-lang.org/old/node/7542
  private def addClasspath(classpath: String) = try {
    val field = classOf[ClassLoader].getDeclaredField("usr_paths")
    field.setAccessible(true)
    val paths = field.get(null).asInstanceOf[Array[String]]
    if (!(paths contains classpath)) {
      field.set(null, paths :+ classpath)
      System.setProperty(
        "java.library.path",
        System.getProperty("java.library.path") + java.io.File.pathSeparator + classpath)
    }
  } catch {
    case _: IllegalAccessException =>
      System.err.println("Scalive: Could not add classpath (insufficient permissions; couldn't modify private variables)")
    case _: NoSuchFieldException =>
      System.err.println("Scalive: Could not add classpath (not Oracle/Sun JVM?)")
  }

  private def startTcpRepl(port: Int) {
    new Thread(new Runnable {
      override def run() {
        val server = new ServerSocket(port)
        val client = server.accept()
        new Repl(client.getInputStream, client.getOutputStream)
      }
    }).start()
  }
}
