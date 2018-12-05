name := "payloadParser"

libraryDependencies ++= Seq("com.typesafe.play" %% "play-json" % "2.6.7",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.tpolecat" %% "doobie-core" % "0.5.2",
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.9.7")
//libraryDependencies += "com.eed3si9n" %% "sbt-assembly" % "sbt0.10.1_0.6"
lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  organization := "com.example",
  scalaVersion := "2.12.7",
  test in assembly := {}
)

lazy val app = (project in file("app")).
  settings(commonSettings: _*).
  settings(
    mainClass in assembly := Some("main"),
    // more settings here ...
 )