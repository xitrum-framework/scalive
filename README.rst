This tool allows you to connect a Scala REPL console to running JVM process
without any special setup.

If you want a Clojure REPL console, try `liverepl <https://github.com/djpowell/liverepl>`_.

Usage
-----

To see a list of running JVM processes on the system, and their
process ids:

::

  livescala

To see a list of class loaders of a specific process:

::

  livescala <pid>

To connect a Scala REPL console to a process:

::

  livescala <pid> <class-loader-id>
