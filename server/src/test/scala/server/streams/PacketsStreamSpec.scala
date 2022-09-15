package server.streams

import cats.effect.IO
import fs2.{CompositeFailure, Stream}
import fs2.concurrent.SignallingRef
import model.{Packet, Packets}
import munit.CatsEffectSuite
import server.Fixtures.*
import server.streams.Packets
import server.{HttpError, HttpTlsError}

import scala.util.chaining.scalaUtilChainingOps
import scala.concurrent.duration.*

class PacketsStreamSpec extends CatsEffectSuite:

  def combinedStreams(interval: FiniteDuration, iter: Int)(
      s1: IO[String],
      s2: IO[String]
  )(shutdown: SignallingRef[IO, Boolean]): Stream[IO, Packets] =
    Packets.stream(interval)(s1, s2)(shutdown).take(iter)

  override val munitTimeout: FiniteDuration = 1.second

  val expected: Vector[Packet] = expectedPackets ++ expectedPackets

  test("Packets stream processes two streams and combines the result") {
    Stream
      .eval(SignallingRef[IO, Boolean](false))
      .flatMap(
        combinedStreams(interval = 0.seconds, iter = 2)(
          rawPackets,
          rawPackets
        )
      )
      .compile
      .lastOrError
      .assertEquals(expected)
  }

  test("Streams are processed concurrently") {
    val delayedStream = IO.sleep(250.millis) >> rawPackets
    Stream
      .eval(SignallingRef[IO, Boolean](false))
      .flatMap(
        combinedStreams(interval = 500.millis, iter = 1)(
          delayedStream,
          delayedStream
        )
      )
      .compile
      .lastOrError
      .assertEquals(expected)
  }

  test("Packets stream propagates errors") {
    interceptIO[CompositeFailure](
      Stream
        .eval(SignallingRef[IO, Boolean](false))
        .flatMap(
          combinedStreams(interval = 500.millis, iter = 1)(
            IO.raiseError(HttpError("Http failed")),
            IO.raiseError(HttpTlsError("HttpTls failed"))
          )
        )
        .compile
        .lastOrError
    ).pipe(
      assertIO(
        _,
        CompositeFailure(HttpTlsError("HttpTls failed"), HttpError("Http failed"))
      )
    )
  }
