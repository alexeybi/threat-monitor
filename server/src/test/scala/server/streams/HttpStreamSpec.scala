package server

import cats.effect.IO
import munit.CatsEffectSuite
import server.streams.Http
import Fixtures.*

class HttpStreamSpec extends CatsEffectSuite:

  test("Http stream processes raw data into packets") {
    Http
      .stream(rawPackets)
      .compile
      .lastOrError
      .assertEquals(expectedPackets)
  }

  test("Http stream failure is propagated") {
    val failedStream = fs2.Stream.raiseError[IO](
      new Exception("failure")
    ) ++ Http.stream(rawPackets).covary[IO]

    failedStream.attempt.compile.lastOrError.map(_.isLeft).assert
  }
