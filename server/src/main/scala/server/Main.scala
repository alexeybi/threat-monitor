package server

import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import fs2.concurrent.Topic
import model.{Packet, Packets}
import server.data.{HttpData, HttpTlsData}
import server.processors.{RawPacketsProcessor, WebRiskProcessor}
import server.streams.Packets
import server.webrisk.WebRisk

import scala.concurrent.duration.*

object Main extends IOApp:
  def run(args: List[String]): IO[ExitCode] =
    for
//            topic           <- Topic[IO, Packets]
      config          <- WebRisk.credentials[IO]
      client           = WebRisk.client[IO]
      webRiskProcessor = WebRiskProcessor(config, client)
      _               <- Packets
                           .stream[IO](5.seconds)(
                             HttpData.rawData[IO],
                             HttpTlsData.rawData[IO]
                           )
                           .through(webRiskProcessor.process)
                           .evalTap(IO.println)
//              .through(topic.publish)
                           .compile
                           .drain
                           .start
      _               <- IO.sleep(20.seconds)
    yield ExitCode.Success
