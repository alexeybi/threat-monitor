package server.data

import cats.effect.kernel.Sync
import server.data.Data.delayedCmd

object HttpData:
  def rawData[F[_]: Sync](interface: String): F[String] =
    delayedCmd(
      "sudo tshark"                                               +
        s" -l -i $interface"                                            +
        " -t a"                                                         +
        " -T fields -e http.host -e ip.dst -e ipv6.dst -e _ws.col.Time" +
        " -f 'tcp port 80'"                                             +
        " -Y http.request"                                              +
        " -E separator=',' -E occurrence=f"                             +
        " -a duration:5"
    )
