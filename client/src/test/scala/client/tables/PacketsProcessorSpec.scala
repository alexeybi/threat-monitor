package client.tables

import client.tables.PacketsProcessor.*
import client.{Fixtures, LaminarUnitSpec}
import com.raquo.domtestutils.matching.Rule
import com.raquo.laminar.api.L.*
import model.{MALWARE, Packet}

import scala.language.implicitConversions
import scala.util.chaining.scalaUtilChainingOps

class PacketsProcessorSpec extends LaminarUnitSpec with Fixtures:

  it("should make rows for normal and malware packets ") {

    val commandStream =
      packetsStream
        .pipe(makeRows)
        .pipe(makeCommands)

    mount(div(children.command <-- commandStream))

    expectChildren(tbody.of(expectedMalware, expectedNormal))
  }
