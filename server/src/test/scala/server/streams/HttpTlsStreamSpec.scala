package server

import cats.effect.IO
import model.{Packet, Packets}
import munit.CatsEffectSuite
import server.streams.{Http, HttpTls}
import Fixtures.*
import server.*

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
      .raiseError[IO](HttpTlsError("failure")) ++ HttpTls.stream(rawPackets).covary[IO]

    val result = failedStream.attempt.compile.lastOrError

    assertIO(result, Left(HttpTlsError("failure")))
  }
