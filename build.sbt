import org.scalajs.linker.interface.ModuleSplitStyle
import Dependencies._

ThisBuild / scalaVersion := Versions.scala3
ThisBuild / version      := "0.1.0"

lazy val monitor = project
  .in(file("."))
  .aggregate(shared.jvm, shared.js, client, server)
  .settings(
    (Compile / run) := Def
      .sequential(
        client / Compile / fastLinkJS,
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
      fs2.value,
      munit.value
    )
  )
  .dependsOn(shared.jvm)

lazy val client = project
  .in(file("client"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    jsEnv                                                 := new net.exoego.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
    Test / requireJsDomEnv                                := true,
    scalaJSUseMainModuleInitializer                       := true,
    (Compile / fastLinkJS / scalaJSLinkerOutputDirectory) := (ThisBuild / baseDirectory).value / "static" / "js",
    (Compile / fullLinkJS / scalaJSLinkerOutputDirectory) := (ThisBuild / baseDirectory).value / "static" / "js",
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)
      .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("monitor")))),
    cleanFiles ++= Seq(
      (ThisBuild / baseDirectory).value / "static" / "js" / "main.js",
      (ThisBuild / baseDirectory).value / "static" / "js" / "main.js.map"
    ),
    libraryDependencies ++= Seq.concat(laminar.value, laminext.value, domtestutils.value),
    scalacOptions ++= compilerOptions
  )
  .dependsOn(shared.js)

lazy val shared = crossProject(JVMPlatform, JSPlatform)
  .in(file("shared"))
  .settings(
    scalacOptions ++= compilerOptions,
    libraryDependencies ++= circe.value
  )

lazy val compilerOptions = Seq(
  "-new-syntax",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfatal-warnings"
)
