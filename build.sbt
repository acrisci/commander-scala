libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4"

lazy val root = (project in file(".")).
  settings(
    organization := "com.github.acrisci",
    name := "commander",
    version := "0.1.0",
    scalaVersion := "2.11.7"
  )

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF")

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
pomExtra :=
  <url>https://github.com/acrisci/commander-scala</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>https://opensource.org/licenses/MIT</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:acrisci/commander-scala</url>
    <connection>scm:git:git@github.com:acrisci/commander-scala.git</connection>
  </scm>
  <developers>
    <developer>
      <id>tonyctl</id>
      <name>Tony Crisci</name>
      <url>http://dubstepdish.com</url>
    </developer>
  </developers>

pgpReadOnly := false
