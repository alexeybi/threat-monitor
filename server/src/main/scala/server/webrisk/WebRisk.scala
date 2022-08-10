package server.webrisk

import cats.effect.kernel.{Async, Resource, Sync}
import cats.effect.unsafe.implicits.*
import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.implicits.{catsSyntaxFlatMapOps, toFlatMapOps}
import cats.syntax.all.toFunctorOps
import com.google.auth.oauth2.{AccessToken, GoogleCredentials}
import io.circe.*
import io.circe.Decoder.decodeList
import io.circe.parser.*
import model.{ThreatType, given}
import org.http4s.*
import org.http4s.FormDataDecoder.formEntityDecoder
import org.http4s.UriTemplate.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.client.*
import org.http4s.client.dsl.io.*
import org.http4s.dsl.io.*
import org.http4s.ember.client.*
import org.http4s.headers.*
import org.http4s.implicits.*

import scala.util.chaining.scalaUtilChainingOps

object WebRisk:

  def client[F[_]: Async]: Resource[F, Client[F]] =
    EmberClientBuilder.default[F].build

  def credentials[F[_]: Async]: F[GoogleCredentials] = Async[F].delay(
    GoogleCredentials.getApplicationDefault
      .createScoped(
        "https://www.googleapis.com/auth/cloud-platform"
      )
  )

  def verifyUri[F[_]: Async](
      url: String,
      config: GoogleCredentials,
      client: Resource[F, Client[F]]
  ): F[Vector[ThreatType]] =
    for
      token    <- retrieveAccessToken(config)
      request  <- webRiskRequest(url, token)
      response <- runSearchUriRequest(client, request)
    yield response

  def retrieveAccessToken[F[_]: Async](
      credentials: GoogleCredentials
  ): F[AccessToken] =
    Async[F].delay(credentials.refreshIfExpired()) >>
      Async[F].delay(credentials.getAccessToken)

  def webRiskRequest[F[_]: Async](uri: String, token: AccessToken): F[Request[F]] =
    Async[F].delay(
      Request[F](
        uri = uri"https://webrisk.googleapis.com/v1/uris:search"
          .withQueryParam("uri", uri)
          .withQueryParam(
            "threatTypes",
            List("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE")
          )
      ).putHeaders(
        Authorization(Credentials.Token(AuthScheme.Bearer, token.getTokenValue)),
        Accept(MediaType.application.json)
      )
    )

  def runSearchUriRequest[F[_]: Async](
      client: Resource[F, Client[F]],
      request: Request[F]
  ): F[Vector[ThreatType]] =
    client.use(
      _.run(request).use(
        _.as[Json].map { json =>
          if json.asObject.forall(_.isEmpty) then Vector.empty
          else
            json.hcursor
              .downField("threat")
              .downField("threatTypes")
              .as[Vector[ThreatType]]
              .getOrElse(Vector.empty)
        }
      )
    )
