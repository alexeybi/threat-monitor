package server

import cats.MonadThrow
import cats.data.Kleisli
import cats.effect.*
import cats.effect.kernel.Async
import cats.implicits.*
import com.comcast.ip4s.*
import fs2.Stream
import fs2.concurrent.Topic
import fs2.io.file.{Files, Path}
import io.circe.syntax.*
import model.{*, given}
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import org.http4s.server.staticcontent.*
import org.http4s.server.websocket.{WebSocketBuilder, WebSocketBuilder2}
import org.http4s.websocket.WebSocketFrame
import org.http4s.{HttpRoutes, Request, Response, StaticFile}

object Server:

  def serverBuilder[F[_]: Async]: EmberServerBuilder[F] =
    EmberServerBuilder
      .default[F]
      .withHost(Host.fromString("0.0.0.0").get)
      .withPort(Port.fromInt(8080).get)

  def httpApp[F[_]: Async](topic: Topic[F, Packets])(
      wsb: WebSocketBuilder2[F]
  ): Kleisli[F, Request[F], Response[F]] =
    Router(
      "static" -> fileService(FileService.Config("static")),
      "/"      -> routes(topic, wsb)
    ).orNotFound

  private def routes[F[_]: Async](
      topic: Topic[F, Packets],
      wsBuilder: WebSocketBuilder2[F]
  ): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ GET -> Root =>
        StaticFile
          .fromPath(Path("static/html/index.html"), Some(request))
          .getOrElseF(Async[F].delay(Response(NotFound)))
      case GET -> Root / "ws"    =>
        wsBuilder.build(
          send = topic
            .subscribe(1)
            .map(packets => WebSocketFrame.Text(packets.asJson.show)),
          receive = _ >> Stream.eval(Async[F].unit)
        )
    }
