package server

import cats.effect.IO
import munit.CatsEffectSuite
import fs2.Stream
import server.streams.Http
import Fixtures.*
import server.*

class HttpStreamSpec extends CatsEffectSuite:

  test("Http stream processes raw data into packets") {
    Http
      .stream(rawPackets)
      .compile
      .lastOrError
      .assertEquals(expectedPackets)
  }

  test("Http stream failure is propagated") {
    val failedStream = Stream.raiseError[IO](
      HttpError("failure")
    ) ++ Http.stream(rawPackets).covary[IO]

    val result = failedStream.attempt.compile.lastOrError

    assertIO(result, Left(HttpError("failure")))
  }
