package scalive

// http://docs.oracle.com/javase/6/docs/jdk/api/attach/spec/index.html
import com.sun.tools.attach.VirtualMachine

import java.io.File
import java.net.URLClassLoader

object AgentLoader {
 /**
   * @param args <jarpath> [class loader id]
   *
   * jarpath is the absolute path this directory:
   *
   * {{{
   * jarpath/
   *   scalive-agent.jar
   *   scalive-client.jar
   *   scalive-repl.jar
   *
   *   2.10.3/
   *     scala-library.jar
   *     scala-compiler.jar
   *     scala-reflect.jar
   *
   *   [Other Scala versions]
   * }}}
   */
  def main(args: Array[String]) {
    if (args.length != 1 && args.length != 2 && args.length != 3) {
      println("Arguments: <jarpath> [pid] [class loader id]")
      return
    }

    addToolsDotJarToClasspath()

    if (args.length == 1) {
      listJvmProcesses()
      return
    }

    val jarpath = args(0)
    val pid     = args(1)
    val clId    = if (args.length == 3) args(2) else ""
    loadAgent(jarpath, pid, clId)
  }

  /**
   * com.sun.tools.attach.VirtualMachine is in tools.jar, which is not in
   * classpath by default:
   *
   * {{{
   * jdk/
   *   bin/
   *     java
   *     javac
   *   jre/
   *     java
   *   lib/
   *     tools.jar
   * }}}
   */
  private def addToolsDotJarToClasspath() {
    val path = System.getProperty("java.home") + "/../lib/tools.jar"
    Classpath.addPath(ClassLoader.getSystemClassLoader.asInstanceOf[URLClassLoader], path)
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

  private def loadAgent(jarpath: String, pid: String, clId: String) {
    val agentJar = Classpath.findJar(jarpath, "scalive-agent")
    val vm       = VirtualMachine.attach(pid)
    val port     = Client.getFreePort()
    vm.loadAgent(agentJar, jarpath + " " + port + " " + clId)

    Runtime.getRuntime.addShutdownHook(new Thread {
      override def run() { vm.detach() }
    })

    Client.connectToRepl(port)
  }
}
