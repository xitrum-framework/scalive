zip directory is the directory that will be zipped when Scalive is released.

::

  zip/
    scalive
    scalive.bat
    scala-library-2.10.3.jar
    scala-compiler-2.10.3.jar
    scala-reflect-2.10.3.jar
    scalive-client_2.10-1.0-SNAPSHOT.jar -> ../../client/target/scala-2.10/scalive-client_2.10-1.0-SNAPSHOT.jar
    scalive-agent_2.10-1.0-SNAPSHOT.jar -> ../../agent/target/scala-2.10/scalive-agent_2.10-1.0-SNAPSHOT.jar
    scalive-repl_2.10-1.0-SNAPSHOT.jar -> ../../repl/target/scala-2.10/scalive-repl_2.10-1.0-SNAPSHOT.jar

While developing:

* Add missing jar files as above
* Run ``sbt package`` in subprojects to update their jar files
* Run ``scalive`` to test
