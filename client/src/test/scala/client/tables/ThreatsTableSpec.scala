package client.tables

import client.tables.ThreatsTable
import client.{Fixtures, LaminarUnitSpec}
import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L.*

class ThreatsTableSpec extends LaminarUnitSpec with Fixtures:

  val expectedTable =
    div.of(
      div.of(
        div.of(
          table.of(
            thead.of(
              tr.of(
                th.of("Threats")
              ),
              tr.of(
                th.of("Url"),
                th.of("IP Address"),
                th.of("Threats"),
                th.of("Timestamp")
              )
            ),
            tbody.of(ExpectedNode.comment, expectedMalware)
          )
        )
      )
    )

  it("should render threats table if it's turned on") {

    val threatsTable = ThreatsTable
      .threats(packetsStream, Signal.fromValue(true))

    mount(threatsTable)

    expectNode(expectedTable)
  }

  it("should not render threats table if it's turned off") {
    val threatsTable = ThreatsTable
      .threats(packetsStream, Signal.fromValue(false))

    mount(threatsTable)

    expectNode(div.of(div))
  }
