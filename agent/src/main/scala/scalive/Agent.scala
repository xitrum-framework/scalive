package scalive

import java.lang.instrument.Instrumentation
import java.net.ServerSocket

object Agent {
  /**
   * @param agentArgs <port>
   */
  def agentmain(agentArgs: String, inst: Instrumentation) {
    println("Scalive agent loaded")

    val args = agentArgs.split(" ")
    val port = args(0).toInt

    new Thread(new Runnable {
      override def run() {
        val server = new ServerSocket(port)
        val client = server.accept()
        new Repl(client.getInputStream, client.getOutputStream)
      }
    }).start()
  }

  def main(args: Array[String]) {
    agentmain(args.mkString(" "), null)
  }
}
