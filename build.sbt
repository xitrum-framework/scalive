organization := "tv.cntt"

name         := "scalive"

version      := "1.0-SNAPSHOT"

autoScalaLibrary := false

javacOptions ++= Seq("-Xlint:deprecation")

// Add tools.jar to classpath
// https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api
unmanagedJars in Compile := (file(System.getProperty("java.home")) / ".." / "lib" * "tools.jar").classpath

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.10.3"

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Main-Class"  -> "scalive.AgentLoader",
  "Agent-Class" -> "scalive.Agent"
)
