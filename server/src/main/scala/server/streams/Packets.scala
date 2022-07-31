package server.streams

import cats.*
import cats.effect.kernel.{Concurrent, Sync, Temporal}
import cats.implicits.*
import fs2.Stream
import model.Packets
import server.data.{HttpData, HttpTlsData}

import scala.concurrent.duration.*

object Packets:
  def stream[F[_]: Sync: Temporal: Concurrent](interval: FiniteDuration)(
      httpData: F[String],
      httpTlsData: F[String]
  ): Stream[F, Packets] =
    Stream.awakeEvery[F](interval) >>
      (HttpTls.stream(httpTlsData) parZipWith Http.stream(httpData)) (Monoid.combine)
