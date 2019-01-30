organization := "tv.cntt"
name         := "scalive"
version      := "1.7.0"

// Scalive is a Java library, we only use SBT to build
scalaVersion     := "2.12.8"
autoScalaLibrary := false
crossPaths       := false  // Do not append Scala versions to the generated artifacts

javacOptions ++= Seq("-Xlint:deprecation")

// Ensure Scalive can run on Java from 8 (Scala 2.12 requires Java 8)
scalacOptions += "-target:jvm-1.8"
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

// scala-compiler already embeds JLine, no need to add JLine dependency separately
libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided"

// Add tools.jar to classpath
// https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api
unmanagedJars in Compile := (file(System.getProperty("java.home")) / ".." / "lib" * "tools.jar").classpath

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Main-Class"  -> "scalive.client.AgentLoader",
  "Agent-Class" -> "scalive.server.Agent"
)
