package server

import cats.effect.IO
import com.comcast.ip4s.{Host, Port}
import fs2.Stream
import fs2.concurrent.Topic
import fs2.io.file.{Files, Path}
import model.Packets
import munit.CatsEffectSuite
import org.http4s.Method.GET
import org.http4s.Status.{NotFound, Ok}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.uri
import org.http4s.{Method, Request, Response, Uri}
import server.Server.*

class ServerSpec extends CatsEffectSuite:

  def check(request: Request[IO])(assert: Response[IO] => IO[Unit])(
      topic: Topic[IO, Packets]
  ): IO[Unit] =
    IO.blocking(
      EmberServerBuilder
        .default[IO]
        .withHost(Host.fromString("localhost").get)
        .withPort(Port.fromInt(8081).get)
        .withHttpWebSocketApp { wsb =>
          val app = httpApp(topic)(wsb)
          app
            .run(request)
            .flatMap(assert)
            .unsafeRunSync()
          app
        }
        .build
        .use(_ => IO.unit)
        .unsafeRunSync()
    )

  test("ServerSpec serves the routes") {

    val request = Request[IO](GET, uri"/")

    Topic[IO, Packets].flatMap(
      check(request)(response =>
        for
          _     <- IO(assertEquals(response.status, Ok))
          index <- Files[IO]
                     .readAll(Path("static/html/index.html"))
                     .through(fs2.text.utf8.decode)
                     .compile
                     .string
          _     <- assertIO(response.bodyText.compile.string, index)
        yield ()
      )
    )
  }

  test("ServerSpec serves web socket route") {

    val request = Request[IO](GET, uri"/ws")

    Topic[IO, Packets].flatMap(
      check(request)(response =>
        for {
          text <- response.bodyText.compile.string
          _    <- assertIO(IO("This is a WebSocket route."), text)
        } yield ()
      )
    )
  }
