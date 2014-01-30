package scalive

// http://docs.oracle.com/javase/6/docs/jdk/api/attach/spec/index.html
import com.sun.tools.attach.VirtualMachine

import java.io.InputStreamReader
import java.net.{InetAddress, ServerSocket, Socket, SocketException}

import scala.io.Source

object Client {
  private val LOCALHOST = InetAddress.getByAddress(Array[Byte](127, 0, 0, 1))

  def main(args: Array[String]) {
    if (args.length != 1 && args.length != 2) {
      println("Arguments: <absolute path to scalive-agent.jar> [pid to connect to]")
      return
    }

    if (args.length == 1) {
      listJvmProcesses()
      return
    }

    val agentJar = args(0)
    val pid      = args(1)
    loadAgent(agentJar, pid)
  }

  private def listJvmProcesses() {
    println("JVM processes:")
    println("#pid\tDisplay name")

    val it = VirtualMachine.list.iterator()
    while (it.hasNext()) {
      val vmd = it.next()
      println(vmd.id + "\t" + vmd.displayName)
    }
  }

  private def loadAgent(agentJar: String, pid: String) {
    val vm   = VirtualMachine.attach(pid)
    val port = getFreePort()
    vm.loadAgent(agentJar, "" + port)

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run() { vm.detach() }
    })

    connectToRepl(port)
  }

  private def getFreePort(): Int = {
    val server = new ServerSocket(0, 0, LOCALHOST)
    val port   = server.getLocalPort
    server.close()
    port
  }

  private def connectToRepl(port: Int) {
    val client = new Socket(LOCALHOST, port)
    val in     = client.getInputStream
    val out    = client.getOutputStream

    println("[Scalive] Attached to remote process at port " + port)

    new Thread(new Runnable {
      override def run() {
        val reader = new InputStreamReader(in)
        var closed = false
        while (!closed) {
          val int = reader.read()
          closed = int < 0
          if (!closed) print(int.toChar)
        }

        println("[Scalive] Connection to remote process closed")
        System.exit(0)
      }
    }).start()

    var closed = false
    while (!closed) {
      try {
        for (ln <- Source.stdin.getLines) {
          out.write(ln.getBytes)
          out.write('\n')
          out.flush()
        }
      } catch {
        case e: SocketException =>
          closed = true
      }
    }
  }
}
