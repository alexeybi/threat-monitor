package server.data

import cats.effect.kernel.Sync
import server.data.Data.delayedCmd

object HttpData:
  def rawData[F[_]: Sync]: F[String] =
    delayedCmd(
      "tshark"                                                    +
        " -l -i en0"                                                    +
        " -t a"                                                         +
        " -T fields -e http.host -e ip.dst -e ipv6.dst -e _ws.col.Time" +
        " -f 'tcp port 80'"                                             +
        " -Y http.request"                                              +
        " -E separator=',' -E occurrence=f"                             +
        " -a duration:5"
    )
