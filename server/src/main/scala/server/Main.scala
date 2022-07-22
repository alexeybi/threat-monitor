package server

import cats.effect.{ExitCode, IO, IOApp}
import fs2.concurrent.Topic
import model.Packets
import server.data.{HttpData, HttpTlsData}
import server.streams.Packets
import scala.concurrent.duration.*

object Main extends IOApp:
  def run(args: List[String]): IO[ExitCode] =
    for
      packetsTopic <- Topic[IO, Packets]
      _ <- Packets
             .stream[IO](5.seconds)(
               HttpTlsData.rawData,
               HttpData.rawData
             )
             .through(packetsTopic.publish)
             .compile
             .drain
             .start
    yield ExitCode.Success
