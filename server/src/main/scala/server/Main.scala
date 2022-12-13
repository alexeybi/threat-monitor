package server

import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import fs2.concurrent.{SignallingRef, Topic}
import model.{Packet, Packets}
import server.Server.*
import server.data.Data.interface
import server.data.{HttpData, HttpTlsData}
import server.processors.{RawPacketsProcessor, WebRiskProcessor}
import server.streams.Packets
import server.webrisk.WebRisk

import scala.concurrent.duration.*

object Main extends IOApp:
  def run(args: List[String]): IO[ExitCode] =
    for
      shutdown        <- SignallingRef[IO, Boolean](false)
      topic           <- Topic[IO, Packets]
      config          <- WebRisk.credentials[IO]
      interface       <- interface[IO]
      client           = WebRisk.client[IO]
      webRiskProcessor = WebRiskProcessor(config, client, shutdown)
      _               <- Packets
                           .stream[IO](5.seconds)(
                             HttpData.rawData(interface),
                             HttpTlsData.rawData(interface)
                           )(shutdown)
                           .through(webRiskProcessor.process)
                           .through(topic.publish)
                           .interruptWhen(shutdown)
                           .compile
                           .drain
                           .start
      _               <- Stream
                           .eval(
                             serverBuilder[IO]
                               .withHttpWebSocketApp(httpApp(topic))
                               .build
                               .useForever
                           )
                           .interruptWhen(shutdown)
                           .compile
                           .drain
    yield ExitCode.Success
