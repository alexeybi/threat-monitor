package server.streams

import cats.effect.IO
import fs2.Stream
import model.{Packet, Packets}
import munit.CatsEffectSuite
import server.streams.Fixtures.*
import server.streams.Packets

import scala.concurrent.duration.*

class PacketsStreamSpec extends CatsEffectSuite:

  def combinedStreams(interval: FiniteDuration, iter: Int)(
      s1: IO[String],
      s2: IO[String]
  ): Stream[IO, Packets] =
    Packets.stream(interval)(s1, s2).take(iter)

  override val munitTimeout: FiniteDuration = 1.second

  val expected: Vector[Packet] = expectedPackets ++ expectedPackets

  test("Packets stream processes two streams and combines the result") {
    combinedStreams(interval = 0.seconds, iter = 2)(
      rawPackets,
      rawPackets
    ).compile.lastOrError
      .assertEquals(expected)
  }

  test("Streams are processed concurrently") {
    val delayedStream = IO.sleep(250.millis) >> rawPackets

    combinedStreams(interval = 500.millis, iter = 1)(
      delayedStream,
      delayedStream
    ).compile.lastOrError
      .assertEquals(expected)
  }
