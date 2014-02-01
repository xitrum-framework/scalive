package scalive;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Classpath {
  private static Method addURL = getAddURL();

  // http://stackoverflow.com/questions/8222976/why-urlclassloader-addurl-protected-in-java
  private static Method getAddURL() {
      try {
          Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
          method.setAccessible(true);
          return method;
      } catch (Exception e) {
          e.printStackTrace();
          return null;
      }
  }

  /**
   * @param jarpath Directory containing the jar
   *
   * @param jarbase Ex: "scalive-agent", not "scalive-agent-1.0.jar"
   */
  public static String findJar(String jarpath, String jarbase) throws Exception {
      File   dir   = new File(jarpath);
      File[] files = dir.listFiles();

      for (int i = 0; i < files.length; i++) {
          File   file = files[i];
          String name = file.getName();
          if (file.isFile() && name.endsWith(".jar") && name.startsWith(jarbase))
              return file.getPath();
      }
      throw new Exception("Could not find " + jarbase + " in " + jarpath);
  }

  public static void addPath(URLClassLoader cl, String path) throws Exception {
      URL url = new File(path).toURI().toURL();
      addURL.invoke(cl, url);
  }

  /** Combination of findJar and addPath. */
  public static void findAndAddJar(URLClassLoader cl, String jarpath, String jarbase) throws Exception {
      String jar = findJar(jarpath, jarbase);
      addPath(cl, jar);
  }
}
