package server.data

import cats.effect.kernel.Sync
import server.data.Data.delayedCmd

import scala.sys.process.*
import scala.util.chaining.*

object HttpTlsData:
  def rawData[F[_]: Sync]: F[String] =
    delayedCmd(
      "tshark"                                                                                     +
        " -l -i en0"                                                                               +
        " -t ad"                                                                                   +
        " -T fields -e tls.handshake.extensions_server_name -e ip.dst -e ipv6.dst -e _ws.col.Time" +
        " -Y 'tls.handshake.extension.type == 0'"                                                  +
        " -E separator=',' -E occurrence=f"                                                        +
        " -a duration:5"
    )
