organization := "tv.cntt"

name         := "scalive-agent"

version      := "1.0-SNAPSHOT"

autoScalaLibrary := false

javacOptions ++= Seq("-Xlint:deprecation")

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Agent-Class" -> "scalive.Agent"
)
