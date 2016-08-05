organization := "tv.cntt"
name         := "scalive"
version      := "1.6"

scalaVersion        := "2.11.6"
libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided"
autoScalaLibrary    := false
crossPaths          := false  // Do not append Scala versions to the generated artifacts

javacOptions ++= Seq("-Xlint:deprecation")

// Ensure Scalive can run on Java 6
scalacOptions += "-target:jvm-1.6"
javacOptions ++= Seq("-source", "1.6", "-target", "1.6")

// Add tools.jar to classpath
// https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api
unmanagedJars in Compile := (file(System.getProperty("java.home")) / ".." / "lib" * "tools.jar").classpath

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Main-Class"  -> "scalive.AgentLoader",
  "Agent-Class" -> "scalive.Agent"
)
