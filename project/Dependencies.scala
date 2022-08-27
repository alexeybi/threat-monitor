import Dependencies.Versions._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

import scala.language.postfixOps

object Dependencies {

  object Versions {
    val fs2Version        = "3.2.10"
    val http4sVersion     = "0.23.13"
    val circeVersion      = "0.14.2"
    val munitVersion      = "1.0.7"
    val googleAuthVersion = "1.9.0"
    val laminarVersion    = "0.14.2"
    val laminextVersion   = "0.14.3"
  }

  val http4s = Seq(
    "http4s-dsl",
    "http4s-core",
    "http4s-circe",
    "http4s-ember-client",
    "http4s-ember-server"
  ).map("org.http4s" %% _ % http4sVersion)

  val googleAuth = Seq(
    "com.google.auth" % "google-auth-library-oauth2-http" % googleAuthVersion
  )

  val fs2 = Def.setting(
    Seq(
      "fs2-core",
      "fs2-io"
    ).map("co.fs2" %%% _ % fs2Version)
  )

  val circe = Def.setting(
    Seq(
      "circe-parser",
      "circe-generic",
      "circe-core",
      "circe-literal"
    ).map("io.circe" %%% _ % circeVersion)
  )

  val munit = Def.setting(
    Seq("org.typelevel" %%% "munit-cats-effect-3" % munitVersion % Test)
  )

  val laminar = Def.setting(
    Seq(
      "com.raquo" %%% "laminar" % laminarVersion
    )
  )

  val laminext = Def.setting(
    Seq(
      "core",
      "websocket-circe"
    ).map("io.laminext" %%% _ % laminextVersion)
  )
}
