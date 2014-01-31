zip directory is the directory that will be zipped when Scalive is released.

::

  zip/
    scalive
    scalive.bat
    scala-compiler-2.10.3.jar
    scala-library-2.10.3.jar
    scala-reflect-2.10.3.jar
    scalive-agent_2.10-1.0-SNAPSHOT.jar -> ../../agent/target/scala-2.10/scalive-agent_2.10-1.0-SNAPSHOT.jar
    scalive-client_2.10-1.0-SNAPSHOT.jar -> ../../client/target/scala-2.10/scalive-client_2.10-1.0-SNAPSHOT.jar

While developing:

* Add scala-compiler.jar, scala-library.jar, and scala-reflect.jar there
* Run ``sbt package`` in projects scalive-agent and scalive-client to update their jar files
* Run ``scalive`` to test
