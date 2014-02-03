organization := "tv.cntt"

name         := "scalive-client"

version      := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

javacOptions ++= Seq("-Xlint:deprecation")

// Add tools.jar to classpath
// https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api
unmanagedJars in Compile := (file(System.getProperty("java.home")) / ".." / "lib" * "tools.jar").classpath

unmanagedSources in Compile += file(
  System.getProperty("user.dir") + "/../agent/src/main/java/scalive/Classpath.java"
)

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Main-Class" -> "scalive.AgentLoader"
)
