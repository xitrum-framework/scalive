scalive-client project is written in Java, not Scala, so that we only need
Java to run. No need for scala-library.jar.

scala-agent project is written in Scala because normally the user wants to use
Scala in the REPL anyway.

REPL itself depends on scala-compiler.jar depends on scala-reflect.jar.
