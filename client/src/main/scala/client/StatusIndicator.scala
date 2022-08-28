package client

import client.PacketsStream.packetsStream
import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.{*, given}
import io.laminext.syntax.core.*
import io.laminext.websocket.circe.WebSocket
import model.Packets
import org.scalajs.dom

object StatusIndicator:

  case class Counter(packets: Int, threats: Int)

  private val packetsCounter       = Var(Counter(0, 0))
  private val updatePacketsCounter = packetsCounter.updater[Counter]((a, b) =>
    Counter(a.packets + b.packets, a.threats + b.threats)
  )

  private def countPackets(packets: Packets): Counter =
    packets.foldLeft(Counter(0, 0))((acc, p) =>
      val Counter(packets, threats) = acc
      p.threatTypes.headOption.fold(Counter(packets + 1, threats))(_ =>
        Counter(packets + 1, threats + 1)
      )
    )

  def status(ws: WebSocket[Packets, Packets]): Div =
    val wsClosed: EventStream[Boolean] = ws.closed.map(!_._2)
    div(
      cls("m-3"),
      div(
        packetsStream.map(countPackets) --> updatePacketsCounter,
        cls("p-2", "row", "justify-content-center"),
        child <-- ws.isConnected.map {
          case true =>
            div(
              cls("col-4", "row", "mx-auto", "justify-content-center"),
              button(
                cls("btn", "bg-light", "text-success"),
                border("none"),
                tpe("button"),
                span(
                  cls("spinner-grow", "spinner-grow-md"),
                  role("status"),
                  dataAttr("aria-hidden")("true")
                ),
                h4(b(" Monitoring packets..."))
              )
            )
          case _    =>
            div(
              cls("col-4", "row", "m-2", "justify-content-center"),
              button(
                cls("btn", "bg-light", "text-danger"),
                border("none"),
                tpe("button"),
                span(
                  child <--
                    wsClosed.map {
                      case true => span("")
                      case _    => span(cls("spinner-grow", "spinner-grow-md"))
                    }
                ),
                role("status"),
                dataAttr("aria-hidden")("true"),
                child <-- wsClosed.map {
                  case true => h4(b("Can't connect to the server."))
                  case _    => h4(b(" Can't connect to the server, trying to reconnect..."))
                }
              )
            )
        }
      ),
      div(
        cls(
          "card",
          "bg-light",
          "col-4",
          "mx-auto"
        ),
        styleAttr("max-width: 14rem;"),
        div(cls("card-header", "text-center"), "Stats"),
        div(
          cls("card-body", "text-center"),
          h5(
            cls("card-title"),
            "Packets: ",
            child.text <-- packetsCounter.signal.map(_.packets)
          ),
          h5(
            cls("card-title", "text-danger"),
            "Threats: ",
            child.text <-- packetsCounter.signal.map(_.threats)
          )
        )
      )
    )
