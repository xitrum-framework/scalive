This tool allows you to connect a Scala REPL console to a running Oracle (Sun) JVM
process without any prior setup at the target process.

If you want a Clojure REPL console, try `liverepl <https://github.com/djpowell/liverepl>`_.

Usage
-----

To see a list of running JVM processes and their process ids:

::

  scalive

To see a list of class loaders of a process:

::

  scalive <pid>

To connect a Scala REPL console to a process:

::

  scalive <pid> <class-loader-id>
