package server

import cats.effect.IO
import model.{Packet, Packets}
import munit.CatsEffectSuite
import server.streams.{Http, HttpTls}
import Fixtures.*

class HttpTlsSpec extends CatsEffectSuite:

  test("HttpTls stream processes raw data into packets") {
    HttpTls
      .stream(rawPackets)
      .compile
      .lastOrError
      .assertEquals(expectedPackets)
  }

  test("HttpTls stream failure is propagated") {
    val failedStream = fs2.Stream
      .raiseError[IO](new Exception("failure")) ++ HttpTls.stream(rawPackets).covary[IO]

    failedStream.attempt.compile.lastOrError.map(_.isLeft).assert
  }
