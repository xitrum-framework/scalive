This tool allows you to connect a Scala REPL console to running Oracle (Sun)
JVM processes without any prior setup at the target process.

[![View demo video on YouTube](http://img.youtube.com/vi/h45QQ45D9P8/0.jpg)](http://www.youtube.com/watch?v=h45QQ45D9P8)

## Download

Download and extract
[scalive-1.0.zip](https://drive.google.com/folderview?id=0B4nP_B5KDxyPdW9xLU5USVJoSzg)
(if you're using enterprise GMail account, you may need to log out to download),
you will see:

```
scalive-1.0/
  scalive
  scalive.bat
  scalive-1.0.jar

  scala-library-2.10.2.jar
  scala-compiler-2.10.2.jar
  scala-reflect-2.10.2.jar

  scala-library-2.10.3.jar
  scala-compiler-2.10.3.jar
  scala-reflect-2.10.3.jar
```

scala-library, scala-compiler, and scala-reflect of the appropriate version
will be loaded to your running JVM process, if they have not been loaded.

For convenience, Scala 2.10.2 and 2.10.3 JARs are preincluded. If your process
is using a different Scala version, you need to manually download the
corresponding JARs and save them as above.

## Usage

Run the shell script `scalive` (*nix) or `scalive.bat` (Windows).

To see a list of running JVM processes and their process IDs:

```
scalive
```

To connect a Scala REPL console to a process:

```
scalive <pid>
```

## How to add your own JARs

Scalive only automatically adds `scala-library.jar`, `scala-compiler.jar`,
`scala-reflect.jar`, and `scalive.jar` to the system classpath. If you want to
load additional classes in other JARs, first add the JAR to the system class loader:

```
val cl         = ClassLoader.getSystemClassLoader.asInstanceOf[java.net.URLClassLoader]
val searchDirs = Array("/dir/containing/the/jar")
val jarbase    = "mylib"  // Will match "mylibxxx.jar", convenient when there's version number in the file name
scalive.Classpath.findAndAddJar(cl, searchDirs, jarbase)
```

Now the trick is just quit the REPL console and open it again. You will be able
to use your classes in the JAR as normal:

```
import mylib.foo.Bar
...
```

[Note that `:cp` doesn't work](http://stackoverflow.com/questions/18033752/cannot-add-a-jar-to-scala-repl-with-the-cp-command).

## How Scalive works

Scalive uses the [Attach API](https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api)
to tell the target process to load an [agent](http://javahowto.blogspot.jp/2006/07/javaagent-option.html).

The agent then creates a TCP server to let the Scalive process interact with the
target process. The Scalive process acts as a client.

See also [liverepl](https://github.com/djpowell/liverepl), a similar REPL
console for Clojure.

## Known issues

1.

For simplicity and to avoid memory leak when you attach/detach many times,
Scalive only supports processes with only the default system class loader,
without additional class loaders (Ex: normal standalone JVM processes, like
[Play](http://www.playframework.com/) or
[Xitrum](http://ngocdaothanh.github.io/xitrum/) in production mode).

Processes with multiple class loaders like
[SBT](http://www.scala-sbt.org/) are not supported.

2.

These features will be added in the future:

* [Use up/down arrows keys to navigate the console history, pasting multiline block of code etc.](https://github.com/ngocdaothanh/scalive/issues/1)
* [Use tab key for autocompletion](https://github.com/ngocdaothanh/scalive/issues/2)
