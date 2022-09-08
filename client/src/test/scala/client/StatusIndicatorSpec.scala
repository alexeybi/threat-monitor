package client

import com.raquo.domtestutils.matching.{ExpectedNode, Rule}
import com.raquo.laminar.api.L.*

class StatusIndicatorSpec extends LaminarUnitSpec:

  val expectedStatusIndicator =
    div.of(
      div.of(
        div.of(
          button.of(
            span.of(ExpectedNode.comment),
            ExpectedNode.comment
          )
        )
      ),
      div.of(
        div.of("Stats"),
        div.of(
          h5.of(
            "Packets: ",
            "0"
          ),
          h5.of(
            "Threats: ",
            "0"
          )
        )
      )
    )

  it("should render default status indicator") {

    mount(StatusIndicator.status(PacketsStream.ws))

    expectNode(expectedStatusIndicator)
  }
