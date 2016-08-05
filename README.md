This tool allows you to connect a Scala REPL console to running Oracle (Sun)
JVM processes without any prior setup at the target process.

[![View demo video on YouTube](http://img.youtube.com/vi/h45QQ45D9P8/0.jpg)](http://www.youtube.com/watch?v=h45QQ45D9P8)

## Download

Download and extract
[scalive-1.6.zip](https://github.com/xitrum-framework/scalive/releases/download/1.6/scalive-1.6.zip),
you will see:

```
scalive-1.6/
  scalive
  scalive.cmd
  scalive-1.6.jar
  jline-2.14.2.jar

  scala-library-2.10.6.jar
  scala-compiler-2.10.6.jar
  scala-reflect-2.10.6.jar

  scala-library-2.11.8.jar
  scala-compiler-2.11.8.jar
  scala-reflect-2.11.8.jar
```

scala-library, scala-compiler, and scala-reflect of the correct version
that your JVM process is using will be loaded, if they have not been loaded.
The REPL console needs these libraries to work.

For example, your process has already loaded scala-library 2.11.8 by itself,
but scala-compiler and scala-reflect haven't been loaded, Scalive will
automatically load their version 2.11.8.

If none of them has been loaded, i.e. your process doesn't use Scala,
Scalive will load the lastest version in the directory.

For your convenience, Scala 2.10.6 and 2.11.8 JARs are included above.

If your process is using a different Scala version, you need to manually
download the corresponding JARs from the Internet and save them in the
same directory as above.

## Usage

Run the shell script `scalive` (*nix) or `scalive.cmd` (Windows).

Run without argument to see the list of running JVM process IDs on your local machine:

```
scalive
```

To connect a Scala REPL console to a process:

```
scalive <process id listed above>
```

## How to load your own JARs to the process

Scalive only automatically loads `scala-library.jar`, `scala-compiler.jar`,
`scala-reflect.jar`, and `scalive.jar` to the system classpath.

If you want to load additional classes in other JARs, first run these in the
REPL console to load the JAR to the system class loader:

```
val cl            = ClassLoader.getSystemClassLoader.asInstanceOf[java.net.URLClassLoader]
val jarSearchDirs = Array("/dir/containing/the/jar")
val jarPrefix     = "mylib"  // Will match "mylib-xxx.jar", convenient when there's version number in the file name
scalive.Classpath.findAndAddJar(cl, jarSearchDirs, jarPrefix)
```

Now the trick is just quit the REPL console and connect it to the target process
again. You will be able to use your classes in the JAR normally:

```
import mylib.foo.Bar
...
```

[Note that `:cp` doesn't work](http://stackoverflow.com/questions/18033752/cannot-add-a-jar-to-scala-repl-with-the-cp-command).

## How Scalive works

Scalive uses the [Attach API](https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api) in Java 6
to tell the target process to load an [agent](http://javahowto.blogspot.jp/2006/07/javaagent-option.html).

The agent then creates a REPL interpreter and a TCP server to let the
Scalive process interact with the interpreter remotely. The Scalive
process acts as a client.

Similar projects:

* [liverepl](https://github.com/djpowell/liverepl)
* [scala-web-repl](https://github.com/woshilaiceshide/scala-web-repl)

## Known issues

1.

For simplicity and to avoid memory leak when you attach/detach many times,
Scalive only supports processes with only the default system class loader,
without additional class loaders. Usually they are standalone JVM processes,
like
[Play](http://www.playframework.com/) or
[Xitrum](http://xitrum-framework.github.io/) in production mode.

Processes with multiple class loaders like Tomcat are currently not supported.

2.

These features will be added in the future:

* [Use up/down arrows keys to navigate the console history, pasting multiline block of code etc.](https://github.com/xitrum-framework/scalive/issues/1)
* [Use tab key for autocompletion](https://github.com/xitrum-framework/scalive/issues/2)
