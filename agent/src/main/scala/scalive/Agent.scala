package scalive

import java.lang.instrument.Instrumentation
import java.net.ServerSocket

object Agent {
  private var portOpen = false

  /**
   * @param agentArgs <server port>
   */
  def agentmain(agentArgs: String, inst: Instrumentation) {
    val port = agentArgs.toInt

    // Need to start a new thread because:
    // - startTcpRepl blocks until a connection comes in
    // - VirtualMachine#loadAgent at the client does not return until this
    //   agentmain method returns
    // - The client only connects to the server after VirtualMachine#loadAgent
    //   returns
    new Thread(new Runnable {
      override def run() { startTcpRepl(port) }
    }).start()

    while (!portOpen) Thread.sleep(100)
  }

  /**
   * @param args <server port>
   */
  def main(args: Array[String]) {
    val port = args(0).toInt
    startTcpRepl(port)
  }

  private def startTcpRepl(port: Int) {
    println("[Scalive] REPL server starts at port " + port)
    val server = new ServerSocket(port)
    portOpen   = true

    val client = server.accept()  // Block until a connection comes in
    val repl   = new Repl(client.getInputStream, client.getOutputStream)
    repl.start()
  }
}
