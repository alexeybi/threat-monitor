package client

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.{*, given}
import io.laminext.syntax.core.*
import io.laminext.websocket.circe.WebSocket
import model.Packets
import org.scalajs.dom

object StatusIndicator:
  def status(ws: WebSocket[Packets, Packets]): Div =
    val wsClosed: EventStream[Boolean] = ws.closed.map(!_._2)
    div(
      cls("p-2", "row justify-content-center"),
      child <-- ws.isConnected.map {
        case true =>
          div(
            cls("col-4 row m-2 justify-content-center"),
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
            cls("col-4 row m-2 justify-content-center"),
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
    )
