import sbt.Keys._

lazy val commonSettings = Seq(
  organization := "stronans.com",
  version := "1.4.0",
  scalaVersion := "2.10.3",
  exportJars := true,
  // This forbids including Scala related libraries into the dependency
  autoScalaLibrary := false,
  // Enables publishing to maven repo
  publishMavenStyle := true,
  // Do not append Scala versions to the generated artifacts
  crossPaths := false
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "MotoZero"
  )

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

libraryDependencies ++= Seq(
  "com.pi4j" % "pi4j-core" % "1.0",
  "com.pi4j" % "pi4j-device" % "1.0",
  "log4j" % "log4j" % "1.2.16",
  "junit" % "junit" % "4.11" % "test",
  "com.jayway.jsonpath" % "json-path-assert" % "0.8.1" % "test",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.3",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.3"
)

mainClass in assembly := Some("com.stronans.motozero.Application")

assemblyMergeStrategy in assembly := {
  case "log4j.properties"	=> MergeStrategy.concat
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}