package server

import server.streams.Packets
import scala.concurrent.duration.*
import Fixtures.*
import munit.CatsEffectSuite
import cats.effect.IO

class PacketsStreamSpec extends CatsEffectSuite:

  def combinedStreams(interval: FiniteDuration, iter: Int)(
      s1: IO[String],
      s2: IO[String]
  ) =
    Packets.stream(interval)(s1, s2).take(iter)

  val expected = expectedPackets ++ expectedPackets
  override val munitTimeout = 2.seconds

  test("Packets stream processes two streams and combines the result") {
    combinedStreams(interval = 0.seconds, iter = 2)(
      rawPackets,
      rawPackets
    ).compile.lastOrError
      .assertEquals(expected)
  }

  test("Streams are processed in parallel") {
    val delayedStream = IO.sleep(0.5.seconds) >> rawPackets

    combinedStreams(interval = 1.second, iter = 1)(
      delayedStream,
      delayedStream
    ).compile.lastOrError
      .assertEquals(expected)
  }
