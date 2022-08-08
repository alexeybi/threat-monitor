import Dependencies.Versions._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import sbt._

import scala.language.postfixOps

object Dependencies {

  object Versions {
    val fs2Version        = "3.2.10"
    val http4sVersion     = "0.23.13"
    val circeVersion      = "0.14.2"
    val scalajsVersion    = "2.2.0"
    val munitVersion      = "1.0.7"
    val googleAuthVersion = "1.9.0"
  }

  val http4s = Seq(
    "http4s-dsl",
    "http4s-core",
    "http4s-circe",
    "http4s-ember-client"
  ).map("org.http4s" %% _ % http4sVersion)

  val googleAuth = Seq(
    "google-auth-library-oauth2-http"
  ).map("com.google.auth" % _ % googleAuthVersion)

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

  val scalajs = Def
    .setting(
      Seq("scalajs-dom")
        .map("org.scala-js" %%% _ % scalajsVersion)
    )

  val munit = Def.setting(
    Seq("munit-cats-effect-3").map("org.typelevel" %%% _ % munitVersion % Test)
  )
}
