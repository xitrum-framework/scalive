package scalive

import java.io.File
import java.net.{URL, URLClassLoader}

// Must write in Java style to avoid scala-library.jar to be used.
// It may have not been added to classpath!
object Classpath {
  // http://stackoverflow.com/questions/8222976/why-urlclassloader-addurl-protected-in-java
  private val addURL = {
    val klass  = classOf[URLClassLoader]
    val method = klass.getDeclaredMethod("addURL", classOf[URL])
    method.setAccessible(true)
    method
  }

  def addPath(cl: ClassLoader, path: String) {
    val url = new File(path).toURI.toURL
    addURL.invoke(cl, url)
  }

  /** Combination of findJar and addPath. */
  def findAndAddJar(cl: ClassLoader, jarpath: String, jarbase: String) {
    val jar = findJar(jarpath, jarbase)
    addPath(cl, jar)
  }

  /**
   * @param jarpath Directory containing the jar
   *
   * @param jarbase Ex: "scalive-agent", not "scalive-agent-1.0.jar"
   */
  def findJar(jarpath: String, jarbase: String): String = {
    val dir   = new File(jarpath)
    val files = dir.listFiles()

    var i = 0
    while (i < files.length) {
      val file = files(i)
      val name = file.getName
      if (file.isFile() && name.endsWith(".jar") && name.startsWith(jarbase))
        return file.getPath

      i += 1
    }
    throw new Exception("Could not find " + jarbase + " in " + jarpath)
  }
}
