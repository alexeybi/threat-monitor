package server.streams

import cats.effect.kernel.Sync
import fs2.Stream
import model.Packets
import server.*

object Http:
  def stream[F[_]: Sync](data: F[String]): Stream[F, Packets] =
    RawPacketsProcessor
      .process(data)
      .handleErrorWith(th => Stream.raiseError(HttpError(th.getMessage())))
