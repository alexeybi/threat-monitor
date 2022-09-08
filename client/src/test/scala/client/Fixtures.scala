package client

import com.raquo.domtestutils.matching.{Rule, RuleImplicits}
import model.{MALWARE, Packet}
import com.raquo.laminar.api.L.*

trait Fixtures extends RuleImplicits:

  val malwarePacket = Packet("url", "address", "timestamp", Vector(MALWARE))

  val normalPacket = malwarePacket.copy(threatTypes = Vector.empty)

  val packetsStream = EventStream.fromValue(Vector(malwarePacket, normalPacket))

  val expectedMalware: Rule = tr.of(
    td.of(
      div.of(
        img.of(
          src is "http://localhost/static/svg/exclamation-circle-fill.svg",
          widthAttr is 20,
          heightAttr is 20
        ),
        malwarePacket.url
      )
    ),
    td.of(malwarePacket.ipAddress),
    td.of(malwarePacket.threatTypes.mkString(",")),
    td.of(malwarePacket.timestamp)
  )

  val expectedNormal: Rule =
    tr.of(
      td.of(normalPacket.url),
      td.of(normalPacket.ipAddress),
      td.of(normalPacket.threatTypes.mkString(",")),
      td.of(normalPacket.timestamp)
    )
