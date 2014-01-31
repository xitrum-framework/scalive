This tool allows you to connect a Scala REPL console to a running Oracle (Sun) JVM
process without any prior setup at the target process.

If you want a Clojure REPL console, try `liverepl <https://github.com/djpowell/liverepl>`_.

Download
--------

Extract `scalive-1.0.zip <TODO>`_ you will see:

::

  scalive/
    scalive
    scalive.bat
    scalive-client-1.0.jar
    scalive-agent-1.0.jar
    scala-library-2.0.3.jar
    scala-compiler-2.0.3.jar
    scala-reflect-2.0.3.jar

Because scala-library-2.0.3.jar, scala-compiler-2.0.3.jar, and scala-reflect-2.0.3.jar
will be loaded to your JVM process, if the process is using a different version,
you may need to manually replace the above jars with the version you need.

Usage
-----

Run the shell script scalive (*nix) or scalive.bat (Windows).

To see a list of running JVM processes and their process ids:

::

  scalive

To connect a Scala REPL console to a process:

::

  scalive <pid>

If there are multiple class loaders in the process, Scalive will list them so
that you can choose:

::

  TODO

Then you run again:

::

  scalive <pid> <class-loader-id>

How it works
------------

Scalive uses the `Attach API <https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api>`_
to tell the target process to load an `agent <http://javahowto.blogspot.jp/2006/07/javaagent-option.html>`_.

The agent creates a TCP server to let the Scalive process interact with the target
process. The Scalive process acts as a client.
