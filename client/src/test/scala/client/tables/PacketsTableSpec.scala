package client.tables

import client.tables.PacketsTable
import client.{Fixtures, LaminarUnitSpec}
import com.raquo.airstream.core.EventStream
import com.raquo.domtestutils.matching.ExpectedNode
import com.raquo.laminar.api.L.*

class PacketsTableSpec extends LaminarUnitSpec with Fixtures:

  val expectedTable =
    div.of(
      div.of(
        table.of(
          thead.of(
            tr.of(
              th.of("Packets")
            ),
            tr.of(
              th.of("Url"),
              th.of("IP Address"),
              th.of("Threats"),
              th.of("Timestamp")
            )
          ),
          ExpectedNode.comment,
          tbody.of(expectedMalware, expectedNormal)
        )
      )
    )

  it("should render packets table") {

    val packetsTable = PacketsTable.packets(packetsStream)

    mount(packetsTable)

    expectNode(expectedTable)
  }
