organization := "tv.cntt"

name         := "scalive-client"

version      := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// Add tools.jar to classpath
// https://blogs.oracle.com/CoreJavaTechTips/entry/the_attach_api
unmanagedJars in Compile := (file(System.getProperty("java.home")) / ".." / "lib" * "tools.jar").classpath

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Main-Class" -> "scalive.AgentLoader"
)

// Copy these to target/xitrum when sbt xitrum-package is run
XitrumPackage.copy("script")
