package server.processors

import munit.CatsEffectSuite
import server.Fixtures.{expectedPackets, rawPackets}

class RawPacketsProcessorSpec extends CatsEffectSuite:
  test("RawPacketsProcessor processes raw strings into packets") {
    val result = RawPacketsProcessor.process(rawPackets).compile.lastOrError
    assertIO(result, expectedPackets)
  }
