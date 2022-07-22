package server.data

import cats.effect.kernel.Sync

import scala.sys.process.*
import scala.util.chaining.scalaUtilChainingOps

object Data:
  def delayedCmd[F[_]: Sync](cmd: String): F[String] =
    Process(cmd) pipe (pb => Sync[F].blocking(pb.!!))
