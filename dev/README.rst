Control flow
------------

::

  AgentLoader ----- attaches Agent ---------------> Target process
                    passes:                         * Agent loads Server
                    * TCP port                      * Server listens on the
                    * jarpaths                        specified TCP port

::

  AgentLoader ----- Client connects to the port --> Target process
                                                    * Server loads Repl
              ----- Keyboard input              -->
              <---- Repl output                 ---

zip directory
-------------

This is the directory that will be zipped when Scalive is released.

::

  zip/
    scalive
    scalive.bat
    scalive_2.10-1.0-SNAPSHOT.jar -> ../../target/scala-2.10/scalive_2.10-1.0-SNAPSHOT.jar

    scala-library-2.10.2.jar
    scala-compiler-2.10.2.jar
    scala-reflect-2.10.2.jar

    scala-library-2.10.3.jar
    scala-compiler-2.10.3.jar
    scala-reflect-2.10.3.jar

While developing:

* Run ``sbt package`` to create/update scalive.jar
* Add missing JARs as above
* Run ``scalive`` to test

Release
-------

Based on the ``zip`` directory above, prepare a directory to be zipped and
released (remember to remove uneccessary files, like .gitignore):

::

  scalive-<version>/
    scalive
    scalive.bat
    scalive-<version>.jar  <-- Doesn't depend on Scala, thus doesn't follow Scala JAR naming

    scala-library-2.10.2.jar
    scala-compiler-2.10.2.jar
    scala-reflect-2.10.2.jar

    scala-library-2.10.3.jar
    scala-compiler-2.10.3.jar
    scala-reflect-2.10.3.jar

Then zip it:

::

  zip -r scalive-<version>.zip scalive-<version>
