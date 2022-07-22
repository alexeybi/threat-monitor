package object model:

  final case class Packet(
      url: String,
      ipAddress: String,
      timestamp: String
  )

  type Packets = Vector[Packet]
