ThisBuild / scalaVersion := "3.1.3"
ThisBuild / version      := "0.1.0"

lazy val malwareMonitor = project
  .in(file("."))
  .aggregate(shared.jvm, shared.js, client, server)
  .settings(
    (Compile / run) := Def
      .sequential(
        client / Compile / fastOptJS,
        (server / Compile / run).toTask("")
      )
      .value
  )

lazy val server = project
  .in(file("server"))
  .settings(
    scalacOptions ++= compilerOptions,
    libraryDependencies ++= Seq(
    "org.typelevel" %%% "munit-cats-effect-3" % "1.0.7" % Test)
  )
  .dependsOn(shared.jvm)

lazy val client = project
  .in(file("client"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalacOptions ++= compilerOptions,
    cleanFiles ++= Seq(
      (ThisBuild / baseDirectory).value / "static" / "js" / "client.js",
      (ThisBuild / baseDirectory).value / "static" / "js" / "client.js.map"
    ),
    (Compile / fastOptJS / artifactPath)   := (ThisBuild / baseDirectory).value / "static" / "js" / "client.js",
    (Compile / fullOptJS / artifactPath)   := (ThisBuild / baseDirectory).value / "static" / "js" / "client.js",
    scalaJSUseMainModuleInitializer        := true,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.2.0"
  )
  .dependsOn(shared.js)

lazy val shared = crossProject(JVMPlatform, JSPlatform)
  .in(file("shared"))
  .settings(
    scalacOptions ++= compilerOptions,
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-generic" % "0.14.2",
      "io.circe" %%% "circe-parser"  % "0.14.2",
      "co.fs2"   %%% "fs2-core"      % "3.2.10",
      "co.fs2"    %% "fs2-io"        % "3.2.10"
    )
  )

val compilerOptions = Seq(
  "-new-syntax",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-Ykind-projector:underscores"
)
