package client.tables

import PacketsProcessor.transformPackets
import com.raquo.airstream.core.{EventStream, Signal}
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.{*, given}
import model.{Packet, Packets}

import scala.util.chaining.scalaUtilChainingOps

object ThreatsTable:

  private val threatsState = Var(Vector.empty[Packet])
  private val addThreats   = threatsState.updater[Packets](_ :++ _)

  def threats(packetsStream: EventStream[Packets], threatsVisible: Signal[Boolean]): Div =

    val stream = packetsStream.map(_.filter(_.threatTypes.nonEmpty))

    div(
      stream --> addThreats,
      child <-- threatsVisible.map {
        case true =>
          div(
            cls(
              "text-center",
              "overflow-auto"
            ),
            div(
              cls("table-responsive-sm"),
              table(
                cls("table", "mx-auto w-75"),
                thead(
                  cls("table-secondary"),
                  tr(
                    th(cls("table-dark"), colSpan(4), "Threats")
                  ),
                  tr(
                    th("Url"),
                    th("IP Address"),
                    th("Threats"),
                    th("Timestamp")
                  )
                ),
                tbody(
                  children <-- threatsState.signal.map(transformPackets)
                )
              )
            )
          )
        case _    => div()
      }
    )
