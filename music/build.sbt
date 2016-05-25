name := "music"

organization := "no.arktekk"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.3",
  "net.databinder.dispatch" %% "dispatch-tagsoup" % "0.11.3",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)
