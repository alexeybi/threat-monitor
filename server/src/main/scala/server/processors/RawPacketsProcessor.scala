package server.processors

import cats.*
import cats.effect.kernel.Sync
import cats.implicits.*
import cats.syntax.group.catsSyntaxSemigroup
import fs2.{text, Pipe, Stream}
import model.{Packet, Packets, ThreatType}

import scala.util.chaining.scalaUtilChainingOps

object RawPacketsProcessor:
  def process[F[_]: Sync](data: F[String]): Stream[F, Packets] =
    Stream.eval(data).through(processRawData)

  private def processRawData[F[_]: Sync: ApplicativeThrow]: Pipe[F, String, Packets] =
    _.through(text.lines)
      .through(cleanup)
      .through(collect)
      .through(deduplicate)

  private def cleanup[F[_]: Sync]: Pipe[F, String, String] =
    _.mapFilter(line =>
      if line.nonEmpty && !line.startsWith(",") && !line.startsWith("*") then
        Some(line.replaceAll(" ", ""))
      else None
    )

  private def collect[F[_]: Sync]: Pipe[F, String, Packets] =
    _.fold(Vector.empty[Packet])((acc, str) =>
      str
        .split(",")
        .pipe(xs =>
          (
            xs.headOption,
            Semigroup.combine(xs.drop(1).headOption, xs.drop(2).headOption),
            xs.drop(3).headOption,
            Vector.empty.some
          ).mapN(Packet.apply).fold(acc)(acc :+ _)
        )
    )

  private def deduplicate[F[_]: Sync]: Pipe[F, Packets, Packets] =
    _.map(_.distinctBy(_.url))
