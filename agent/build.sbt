organization := "tv.cntt"

name         := "scalive-agent"

version      := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Agent-Class" -> "scalive.Agent"
)

libraryDependencies <++= scalaVersion { v => Seq(
 "org.scala-lang" % "scala-compiler" % v
)}

XitrumPackage.copy()
