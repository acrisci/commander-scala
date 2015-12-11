libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4"

lazy val root = (project in file(".")).
  settings(
    organization := "com.github.acrisci",
    name := "commander",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.11.7"
  )
