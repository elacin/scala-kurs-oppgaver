name := "webframework"

organization := "no.arktekk"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5",
  "org.eclipse.jetty" % "jetty-server" % "8.0.4.v20111024",
  "org.eclipse.jetty" % "jetty-servlet" % "8.0.4.v20111024",
  "org.scalatest" %% "scalatest" % "2.2.6" % Test
)
