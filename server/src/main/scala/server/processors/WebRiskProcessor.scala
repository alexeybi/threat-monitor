package server.processors

import cats.effect.kernel.{Concurrent, Resource}
import cats.effect.{Async, IO}
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxFlatMapOps}
import cats.syntax.all.{catsSyntaxApplicativeError, toFunctorOps}
import com.google.auth.oauth2.GoogleCredentials
import fs2.concurrent.SignallingRef
import fs2.{Pipe, Stream}
import model.{Packet, Packets}
import org.http4s.client.Client
import server.*
import server.webrisk.WebRisk.*

case class WebRiskProcessor[F[_]: Async](process: Pipe[F, Packets, Packets])

object WebRiskProcessor:
  def apply[F[_]: Async](
      config: GoogleCredentials,
      client: Resource[F, Client[F]],
      shutdown: SignallingRef[F, Boolean]
  ): WebRiskProcessor[F] =
    new WebRiskProcessor[F](
      process(packet =>
        verifyUri(packet.url, config, client)
          .map(threats => packet.copy(threatTypes = threats))
          .handleErrorWith(th =>
            shutdown.set(true) >> Async[F]
              .raiseError(WebRiskError(th.getMessage))
          )
      )
    )

  def process[F[_]: Async](f: Packet => F[Packet]): Pipe[F, Packets, Packets] =
    _.evalMap(Async[F].parTraverseN(8)(_)(f))
