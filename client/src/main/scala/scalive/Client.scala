package scalive

import java.io.InputStreamReader
import java.net.{InetAddress, ServerSocket, Socket, SocketException}
import scala.io.Source

object Client {
  private val LOCALHOST = InetAddress.getByAddress(Array[Byte](127, 0, 0, 1))

  def getFreePort(): Int = {
    val server = new ServerSocket(0, 0, LOCALHOST)
    val port   = server.getLocalPort
    server.close()
    port
  }

  def connectToRepl(port: Int) {
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
