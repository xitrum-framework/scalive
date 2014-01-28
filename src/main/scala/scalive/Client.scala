package scalive

// http://docs.oracle.com/javase/6/docs/jdk/api/attach/spec/index.html
import com.sun.tools.attach.VirtualMachine

object Client {
  def main(args: Array[String]) {
    listJvmProcesses()
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
}
