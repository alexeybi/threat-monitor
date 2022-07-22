package server.streams
import cats.effect.kernel.Sync
import cats.{ApplicativeThrow, MonadThrow, Show}
import fs2.*
import model.Packets
import server.data.HttpData
import server.streams.StreamProcessor

object Http:
  def stream[F[_]: Sync](data: F[String]): Stream[F, Packets] =
    StreamProcessor
      .processStream(data)
      .handleErrorWith(err =>
        Stream.raiseError(
          IllegalStateException(
            s"Error occurred while processing the Http stream: ${err.getMessage}"
          )
        )
      )
