package scalive

// http://docs.oracle.com/javase/6/docs/jdk/api/attach/spec/index.html
import com.sun.tools.attach.VirtualMachine

import java.net.{InetAddress, ServerSocket, Socket}

object Client {
  private val LOCALHOST = InetAddress.getByAddress(Array[Byte](127, 0, 0, 1))

  def main(args: Array[String]) {
    if (args.isEmpty) {
      listJvmProcesses()
      return
    }

    val pid = args(0)
    listClassLoaders(pid)
  }

  private def listJvmProcesses() {
    println("JVM processes:")
    println("pid\tDisplay name")

    val it = VirtualMachine.list.iterator()
    while (it.hasNext()) {
      val vmd = it.next()
      println(vmd.id + "\t" + vmd.displayName)
    }
  }

  private def listClassLoaders(pid: String) {
    println("Attach to pid " + pid)

    val vm        = VirtualMachine.attach(pid)
    val agentJar  = "/Users/ngoc/src/scalive/agent/target/xitrum/lib/scalive-agent_2.10-1.0-SNAPSHOT.jar"
    val port      = getFreePort()
    val classpath = "/Users/ngoc/src/scalive/agent/target/xitrum/lib"
    val agentArgs = Seq(port, classpath).mkString(" ")
    vm.loadAgent(agentJar, agentArgs)

    connectToRepl(port)
  }

  private def getFreePort(): Int = {
    val server = new ServerSocket(0, 0, LOCALHOST)
    val port   = server.getLocalPort
    server.close()
    port
  }

  private def connectToRepl(port: Int) {
    import java.io._
    val client = new Socket(LOCALHOST, port)
    val in     = client.getInputStream
    val out    = client.getOutputStream

    new Thread(new Runnable {
      override def run() {
        val r = new InputStreamReader(in)
        while (true) print(r.read().toChar)
      }
    }).start()

    while (true) {
      for (ln <- io.Source.stdin.getLines) {
        out.write(ln.getBytes)
        out.write('\n')
        out.flush()
      }
    }
  }
}
