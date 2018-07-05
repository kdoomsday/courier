import scalariform.formatter.preferences._

name := """courier"""


lazy val commonSettings = Seq(
  version := "1.1",
  scalaVersion in ThisBuild := "2.12.6",
  libraryDependencies ++= testDependencies,
  wartremoverErrors ++= Warts.unsafe,
  parallelExecution in Global := false,
  testFrameworks += new TestFramework("utest.runner.Framework"),
  scalacOptions ++= commonScalacOptions
)

lazy val root   = (project in file("."))
                    .settings(commonSettings: _*)
                    .aggregate(core, client, server)

lazy val core   = (project in file("core")).
                    settings(commonSettings: _*)

lazy val client = (project in file("client") dependsOn core).
                    settings(commonSettings: _*)

lazy val server = (project in file("server") dependsOn core)
                    .settings(commonSettings: _*)
                    .settings(libraryDependencies ++= http4sSettings)


lazy val testDependencies = Seq(
  "com.lihaoyi"   %% "utest"     % "0.6.3" % "test"
)


val http4sVersion = "0.18.13"
lazy val http4sSettings = Seq(
  "org.http4s" %% "http4s-dsl"          % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion
)

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)

fork in run := true

lazy val commonScalacOptions = List(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture",                          // Turn on future language features.
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-value-discard",              // Warn when non-Unit expression results are unused.
  "-Ypartial-unification"
)
