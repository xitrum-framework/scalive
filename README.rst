This tool allows you to connect a Scala REPL console to a running Oracle (Sun) JVM
process without any prior setup at the target process.

If you want a Clojure REPL console, try `liverepl <https://github.com/djpowell/liverepl>`_.

TODO: Add a YouTube video here
http://stackoverflow.com/questions/4279611/how-to-embed-a-video-into-github-readme-md

Download
--------

Extract `scalive-1.0.zip <TODO>`_ you will see:

::

  scalive/
    scalive
    scalive.bat
    scalive-client-1.0.jar
    scalive-agent-1.0.jar
    scalive-repl-1.0.jar
    2.10.2/
      scala-library-2.10.2.jar
      scala-compiler-2.10.2.jar
      scala-reflect-2.10.2.jar
    2.10.3/
      scala-library-2.10.3.jar
      scala-compiler-2.10.3.jar
      scala-reflect-2.10.3.jar

scala-library, scala-compiler, and scala-reflect of the appropriate version
will be loaded to your running JVM process, if they have not been loaded.

JARs for Scala 2.10.2 and 2.10.3 are preincluded for convenience. If your Scala
process is using a different version, you need to manually download the
corresponding JARS and save them as above.

Usage
-----

Run the shell script ``scalive`` (*nix) or ``scalive.bat`` (Windows).

To see a list of running JVM processes and their process ids:

::

  scalive

To connect a Scala REPL console to a process:

::

  scalive <pid>

If there are multiple class loaders in the process, Scalive will list them so
that you can choose. Then you run again:

::

  scalive <pid> <class-loader-id>

How it works
------------

Scalive uses the `Attach API <https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api>`_
to tell the target process to load an `agent <http://javahowto.blogspot.jp/2006/07/javaagent-option.html>`_.

The agent then creates a TCP server to let the Scalive process interact with the target
process. The Scalive process acts as a client.

Known issues
------------

1.

Please read:
http://www.scala-lang.org/old/node/8002

The Scala REPL console needs to know the classpath used by the target process so
that it can compile the code you type at the console on the fly. Scalive tries
to get the classpath from the class loader for you.

It's easy when the class loader is of type `URLClassLoader <http://docs.oracle.com/javase/7/docs/api/java/net/URLClassLoader.html>`_
(just call ``getURLs``). But when the class loader is of other type
(like `ClasspathFilter <http://www.scala-sbt.org/release/api/index.html#sbt.classpath.ClasspathFilter>`_
when the process is started by `SBT <http://www.scala-sbt.org/>`_), Scalive must
solve the problem in the "case by case" style.

Currently, Scalive only supports:

* Single class loader process (normal standalone JVM process, like
  `Play <http://www.playframework.com/>`_ or
  `Xitrum <http://ngocdaothanh.github.io/xitrum/>`_).
* SBT.

If your process/class loader is not supported, please `create an issue <https://github.com/ngocdaothanh/scalive/issues>`_.

2.

These features will be added in the future:

* `Use up/down arrows keys to navigate the console history, pasting multiline
  block of code etc. <https://github.com/ngocdaothanh/scalive/issues/1>`_
* `Use tab key for autocompletion <https://github.com/ngocdaothanh/scalive/issues/2>`_
