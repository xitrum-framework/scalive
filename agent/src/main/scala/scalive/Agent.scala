package scalive

import java.lang.instrument.Instrumentation

object Agent {
  def agentmain(agentArgs: String, inst: Instrumentation) {
    println("Agent loaded")
  }
}
