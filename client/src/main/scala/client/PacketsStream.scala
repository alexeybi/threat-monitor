package client

import io.laminext.websocket.circe.*
import io.laminext.syntax.core.*
import com.raquo.laminar.api.L.{*, given}
import cats.implicits.*
import com.raquo.laminar.CollectionCommand
import org.scalajs.dom
import model.{*, given}

import scala.util.chaining.scalaUtilChainingOps

object PacketsStream:

  val ws: WebSocket[Packets, Packets] =
    WebSocket
      .url("ws://0.0.0.0:8080/ws")
      .json[Packets, Packets]
      .build(managed = true)

  def packetsStream: EventStream[Packets] =
    ws.received.filter(_.nonEmpty)
