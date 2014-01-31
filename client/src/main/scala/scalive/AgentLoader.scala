package scalive

// http://docs.oracle.com/javase/6/docs/jdk/api/attach/spec/index.html
import com.sun.tools.attach.VirtualMachine

object AgentLoader {
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
    val port = Client.getFreePort()
    vm.loadAgent(agentJar, "" + port)

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run() { vm.detach() }
    })

    Client.connectToRepl(port)
  }
}
