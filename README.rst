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
    scala-library-2.10.3.jar
    scala-compiler-2.10.3.jar
    scala-reflect-2.10.3.jar

scala-library-2.10.3.jar, scala-compiler-2.10.3.jar, and scala-reflect-2.10.3.jar
will be loaded to your running JVM process, if they have not been loaded.

Note that, for example, if your process have scala-library-2.0.2 but does not
have scala-compiler-2.0.2.jar and scala-reflect-2.0.2.jar, for best version
compatibility safety, you may need to manually download the 2.0.2 jars and
replace the above 2.10.3 jars with the downloaded ones.

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
