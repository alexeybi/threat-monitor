package client

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.{*, given}
import io.laminext.syntax.core.*
import io.laminext.websocket.circe.WebSocket
import model.Packets
import org.scalajs.dom

object NavBar:

  private val threatsVisibleState: Var[Boolean] = Var(false)

  val threatsTableVisible: StrictSignal[Boolean] = threatsVisibleState.signal

  def navbar: Element =
    div(
      nav(
        cls("navbar bg-dark"),
        div(
          cls("container-fluid"),
          a(
            cls("navbar-brand"),
            img(
              cls("d-inline-block align-text-top"),
              src("/static/svg/bug-fill.svg"),
              widthAttr(30),
              heightAttr(24)
            ),
            b(cls("text-warning"), "Threat Monitor")
          ),
          div(
            cls(
              "row",
              "d-flex",
              "me-2",
              "form-check",
              "form-switch",
              "form-check-reverse"
            ),
            label(
              cls("form-check-label", "text-light", "d-flex", "me-2"),
              forId("flexSwitchCheckDefault"),
              b("Threats table")
            ),
            input(
              cls("form-check-input"),
              tpe("checkbox"),
              role("switch"),
              idAttr("flexSwitchCheckDefault"),
              defaultChecked(false),
              onInput.mapToChecked --> threatsVisibleState.writer
            )
          )
        )
      )
    )
