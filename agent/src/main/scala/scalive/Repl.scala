package scalive

import java.io.{BufferedReader, PipedReader, PipedWriter, PrintWriter}

import scala.tools.nsc.interpreter.ILoop
import scala.tools.nsc.Settings

object Repl {
  val pipedWriter = new PipedWriter

  val settings = new Settings
  settings.usejavacp.value = true

  val repl = new ILoop(
    new BufferedReader(new PipedReader(pipedWriter)),
    new PrintWriter(System.out)
  )

  def write(src: String) {
    new Thread(new Runnable {
      override def run() {
        var i = 1

        pipedWriter.append("var j = 1\n")
        while (i < 20) {
          pipedWriter.append("println(j)\n")
          pipedWriter.append("j += 1\n")
          i += 1
        }

        Thread.sleep(100000)
//        repl.closeInterpreter()
      }
    }).start()

    repl.process(settings)
  }
}
