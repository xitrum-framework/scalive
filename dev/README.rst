zip directory is the directory that will be zipped when Scalive is released.

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
