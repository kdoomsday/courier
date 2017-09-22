import scalariform.formatter.preferences._

name := """courier"""


lazy val commonSettings = Seq(
  version := "1.1",
  scalaVersion := "2.12.2"
)

lazy val root   = (project in file("."))
                    .settings(commonSettings: _*)
                    .aggregate(core, client, server)

lazy val core   = (project in file("core")).
                    settings(commonSettings: _*)

lazy val client = (project in file("client") dependsOn core).
                    settings(commonSettings: _*)

lazy val server = (project in file("server") dependsOn core).
                    settings(commonSettings: _*)

// libraryDependencies ++= Seq(
//   "com.typesafe.akka" %% "akka-stream" % "2.5.4"
// )

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)

fork in run := true
