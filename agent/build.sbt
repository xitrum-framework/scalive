organization := "tv.cntt"

name         := "scalive-agent"

version      := "1.0-SNAPSHOT"

//autoScalaLibrary := false
scalaVersion := "2.10.3"

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Agent-Class" -> "scalive.Agent"
)

unmanagedSources in Compile += file(
  System.getProperty("user.dir") + "/../client/src/main/scala/scalive/Classpath.scala"
)
