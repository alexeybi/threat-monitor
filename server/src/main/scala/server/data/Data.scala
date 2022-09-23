package server.data

import cats.syntax.all.toFunctorOps
import cats.effect.kernel.Sync

import scala.sys.process.*
import scala.util.chaining.scalaUtilChainingOps

object Data:
  def delayedCmd[F[_]: Sync](cmd: String): F[String] =
    Process(cmd) pipe (pb => Sync[F].blocking(pb.!!))

  def interface[F[_]: Sync]: F[String] =
    Sync[F].blocking(System.getProperty("os.name")).map {
      case "Linux" => "wlan0"
      case _       => "en0"
    }
