package server.streams

import cats.effect.kernel.Sync
import cats.{ApplicativeThrow, Show}
import fs2.{Pipe, Stream}
import model.Packets
import server.data.HttpTlsData
import server.streams.StreamProcessor

object HttpTls:
  def stream[F[_]: Sync](data: F[String]): Stream[F, Packets] =
    StreamProcessor
      .processStream(data)
      .handleErrorWith(err =>
        Stream.raiseError(
          IllegalStateException(
            s"Error occurred while processing the HttpTls stream: $err"
          )
        )
      )
