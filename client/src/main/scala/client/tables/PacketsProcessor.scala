package client.tables

import com.raquo.airstream.core.EventStream
import com.raquo.laminar.CollectionCommand
import com.raquo.laminar.api.L.{*, given}
import model.Packets

import scala.util.chaining.scalaUtilChainingOps

object PacketsProcessor:

  def makeRows(
      packetsStream: EventStream[Packets]
  ): EventStream[Vector[Element]] =
    packetsStream.map(transformPackets)

  def makeCommands(
      elementsStream: EventStream[Vector[Element]]
  ): EventStream[ChildrenCommand] =
    elementsStream.map(elems =>
      elems.foldLeft[Element](tbody())(_ amend _).pipe(CollectionCommand.Prepend(_))
    )

  def transformPackets(packets: Packets): Vector[Element] =
    packets.map(packet =>
      val maybeThreats = packet.threatTypes.headOption
      tr(
        cls(maybeThreats.fold("table-success")(_ => "table-danger")),
        maybeThreats.fold(td(packet.url))(_ =>
          td(
            div(
              img(
                cls("mx-2"),
                src("/static/svg/exclamation-circle-fill.svg"),
                widthAttr(20),
                heightAttr(20)
              ),
              packet.url
            )
          )
        ),
        td(packet.ipAddress),
        td(packet.threatTypes.mkString(",")),
        td(packet.timestamp)
      )
    )
