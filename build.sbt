organization := "tv.cntt"

name         := "scalive"

version      := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// Add tools.jar to classpath
// https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api
unmanagedJars in Compile := (file(System.getProperty("java.home")) / ".." / "lib" * "tools.jar").classpath

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Agent-Class" -> "scalive.Agent",
  "Main-Class"  -> "scalive.Client"
)

libraryDependencies <++= scalaVersion { v => Seq(
 "org.scala-lang" % "scala-compiler" % v,
 "org.scala-lang" % "jline"          % v
)}

// Copy these to target/xitrum when sbt xitrum-package is run
XitrumPackage.copy("script")
