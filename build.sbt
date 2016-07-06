import scalariform.formatter.preferences._

name := """courier"""


lazy val commonSettings = Seq(
  version := "1.1",
  scalaVersion := "2.11.7"
)

lazy val root   = (project in file(".")).
                    settings(commonSettings: _*)

lazy val core   = (project in file("core")).
                    settings(commonSettings: _*)

lazy val client = (project in file("client") dependsOn core).
                    settings(commonSettings: _*)

lazy val server = (project in file("server") dependsOn core).
                    settings(commonSettings: _*)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % "2.0.2"
)

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)

fork in run := true
