package scalive

import java.io.File
import java.net.{URL, URLClassLoader}

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

  /** @param jarbase Ex: "scalive-agent", not "scalive-agent-1.0.jar" */
  def findJar(jarpath: String, jarbase: String): String = {
    val dir   = new File(jarpath)
    val files = dir.listFiles()
    for (file <- files) {
      val name = file.getName
      if (file.isFile() && name.endsWith(".jar") && name.startsWith(jarbase))
        return file.getPath
    }
    throw new Exception("Could not find " + jarbase + " in " + jarpath)
  }
}
