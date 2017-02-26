name := "akka-http-daemon"
version := "1.1.0"

organization := "com.tinylabproductions"
scalaVersion := "2.12.1"
scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation", "-Xfatal-warnings"
)
fork in run := true

libraryDependencies ++= Seq(
  // Testing
  "org.specs2" %% "specs2-core" % "3.8.6" % "test"
)
// Needed for specs2
scalacOptions in Test ++= Seq("-Yrangepos")

libraryDependencies ++= Vector(
  "com.typesafe.akka" %% "akka-http" % "10.0.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
)