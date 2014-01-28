package scalive

// http://docs.oracle.com/javase/6/docs/jdk/api/attach/spec/index.html
import com.sun.tools.attach.VirtualMachine

object Client {
  def main(args: Array[String]) {
    //listJvmProcesses()
    println(args.toList)
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

    val vm = VirtualMachine.attach(pid)
    vm.loadAgent("/Users/ngoc/src/scalive/target/scala-2.10/scalive_2.10-1.0-SNAPSHOT.jar")
  }
}
