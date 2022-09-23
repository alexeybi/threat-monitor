package server.data

import cats.effect.kernel.Sync
import server.data.Data. delayedCmd

object HttpTlsData:
  def rawData[F[_]: Sync](interface: String): F[String] =
    delayedCmd(
      "tshark"                                                                               +
        s" -l -i $interface"                                                                               +
        " -t a"                                                                                    +
        " -T fields -e tls.handshake.extensions_server_name -e ip.dst -e ipv6.dst -e _ws.col.Time" +
        " -Y 'tls.handshake.extension.type == 0'"                                                  +
        " -E separator=',' -E occurrence=f"                                                        +
        " -a duration:5"
    )
