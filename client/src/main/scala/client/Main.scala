package client

import com.raquo.laminar.api.L.{*, given}
import PacketsStream.*
import NavBar.*
import client.StatusIndicator.status
import client.tables.PacketsTable.packets
import client.tables.ThreatsTable.threats
import org.scalajs.dom

@main def run =
  renderOnDomContentLoaded(
    dom.document.querySelector("#app"),
    div(
      ws.connect,
      navbar,
      status(ws),
      threats(packetsStream, threatsTableVisible),
      packets(packetsStream)
    )
  )
