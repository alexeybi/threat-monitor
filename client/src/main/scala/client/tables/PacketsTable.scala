package client.tables

import PacketsProcessor.{makeCommands, makeRows}
import com.raquo.laminar.CollectionCommand
import com.raquo.laminar.api.L.{*, given}
import model.Packets

import scala.util.chaining.scalaUtilChainingOps

object PacketsTable:

  def packets(
      packetsStream: EventStream[Packets]
  ): Div =
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
              th(cls("table-dark"), colSpan(4), "Packets")
            ),
            tr(
              th("Url"),
              th("IP Address"),
              th("Threats"),
              th("Timestamp")
            )
          ),
          children.command <-- packetsStream
            .pipe(makeRows)
            .pipe(makeCommands)
        )
      )
    )
