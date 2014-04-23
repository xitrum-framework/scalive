organization := "tv.cntt"

name         := "scalive"

version      := "1.2-SNAPSHOT"

scalaVersion := "2.11.0"

autoScalaLibrary := false

javacOptions ++= Seq("-Xlint:deprecation")

// Ensure Scalive can run on Java 6
scalacOptions += "-target:jvm-1.6"

// Ensure Scalive can run on Java 6
javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

// Add tools.jar to classpath
// https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api
unmanagedJars in Compile := (file(System.getProperty("java.home")) / ".." / "lib" * "tools.jar").classpath

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.11.0"

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Main-Class"  -> "scalive.AgentLoader",
  "Agent-Class" -> "scalive.Agent"
)