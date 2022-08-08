import Dependencies._

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
    libraryDependencies ++= Seq.concat(
      http4s,
      googleAuth,
      munit.value
    )
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
    (Compile / fastOptJS / artifactPath) := (ThisBuild / baseDirectory).value / "static" / "js" / "client.js",
    (Compile / fullOptJS / artifactPath) := (ThisBuild / baseDirectory).value / "static" / "js" / "client.js",
    scalaJSUseMainModuleInitializer      := true,
    libraryDependencies ++= scalajs.value
  )
  .dependsOn(shared.js)

lazy val shared = crossProject(JVMPlatform, JSPlatform)
  .in(file("shared"))
  .settings(
    scalacOptions ++= compilerOptions,
    libraryDependencies ++= Seq.concat(
      fs2.value,
      circe.value
    )
  )

lazy val compilerOptions = Seq(
  "-new-syntax",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-Ykind-projector:underscores"
)
