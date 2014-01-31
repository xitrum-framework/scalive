organization := "tv.cntt"

name         := "scalive-agent"

version      := "1.0-SNAPSHOT"

autoScalaLibrary := false

packageOptions in (Compile, packageBin) += Package.ManifestAttributes(
  "Agent-Class" -> "scalive.Agent"
)
