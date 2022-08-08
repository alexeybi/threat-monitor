package server.processors

import cats.effect.kernel.{Concurrent, Resource}
import cats.effect.{Async, IO}
import cats.syntax.all.toFunctorOps
import com.google.auth.oauth2.GoogleCredentials
import fs2.{Pipe, Stream}
import model.{Packet, Packets}
import org.http4s.client.Client
import server.*
import server.webrisk.WebRisk.*

case class WebRiskProcessor[F[_]: Async](process: Pipe[F, Packets, Packets])

object WebRiskProcessor:
  def apply[F[_]: Async](
      config: GoogleCredentials,
      client: Resource[F, Client[F]]
  ): WebRiskProcessor[F] =
    new WebRiskProcessor[F](
      process(packet =>
        verifyUri(packet.url, config, client)
          .map(threats => packet.copy(threatTypes = threats))
      )
    )

  def process[F[_]: Async](f: Packet => F[Packet]): Pipe[F, Packets, Packets] =
    _.evalMap(Async[F].parTraverseN(8)(_)(f))
      .handleErrorWith(th => Stream.raiseError(WebRiskError(th.getMessage)))
