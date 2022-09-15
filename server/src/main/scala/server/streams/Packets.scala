package server.streams

import cats.*
import cats.effect.kernel.{Concurrent, Sync, Temporal}
import cats.implicits.*
import fs2.{RaiseThrowable, Stream}
import fs2.concurrent.SignallingRef
import model.Packets
import server.data.{HttpData, HttpTlsData}

import scala.concurrent.duration.*

object Packets:
  def stream[F[_]: Sync: Temporal: Concurrent: RaiseThrowable](
      interval: FiniteDuration
  )(
      httpData: F[String],
      httpTlsData: F[String]
  )(
      shutdown: SignallingRef[F, Boolean]
  ): Stream[F, Packets] =
    Stream.awakeEvery[F](interval) >>
      (HttpTls.stream(httpTlsData) parZipWith Http.stream(httpData))(
        Monoid.combine
      ).handleErrorWith(th => Stream.eval(shutdown.set(true)) >> Stream.raiseError(th))
        .interruptWhen(shutdown)
