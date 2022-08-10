import cats.Show
import io.circe.generic.semiauto.*
import io.circe.parser.*
import io.circe.{Decoder, Encoder, HCursor}

package object model:

  final case class Packet(
      url: String,
      ipAddress: String,
      timestamp: String,
      threatTypes: Vector[ThreatType]
  )

  type Packets = Vector[Packet]

  sealed trait ThreatType
  case object MALWARE            extends ThreatType
  case object SOCIAL_ENGINEERING extends ThreatType
  case object UNWANTED_SOFTWARE  extends ThreatType

  given Encoder[ThreatType] = Encoder.encodeString.contramap[ThreatType](_.toString)

  given Decoder[ThreatType] = Decoder.decodeString.emap {
    case "SOCIAL_ENGINEERING" => Right(SOCIAL_ENGINEERING)
    case "UNWANTED_SOFTWARE"  => Right(UNWANTED_SOFTWARE)
    case "MALWARE"            => Right(MALWARE)
  }

  given Encoder[Packets] = Encoder.encodeVector

  given Decoder[Packets] = Decoder.decodeVector

  given Encoder[Packet] = deriveEncoder

  given Decoder[Packet] = deriveDecoder

  given Show[Packet] = Show.fromToString

  given Show[Packets] = Show.fromToString
