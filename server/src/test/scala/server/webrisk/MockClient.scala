package server.streams.webrisk

import cats.effect.IO
import cats.effect.kernel.Resource
import io.circe.Json
import org.http4s.client.Client
import org.http4s.{EntityEncoder, HttpApp, Response, Status}
import cats.implicits.catsSyntaxApplicativeId

import scala.util.chaining.scalaUtilChainingOps

object MockClient:

  def clientWithResponse(status: Status, response: String)(using
      EntityEncoder[IO, Json]
  ): Resource[IO, Client[IO]] =
    Client
      .fromHttpApp(
        HttpApp[IO](_ => Response[IO](status).withEntity(response).pure[IO])
      )
      .pipe(Resource.pure)
