package server.processors

import cats.effect.IO
import cats.effect.kernel.Resource
import cats.implicits.catsSyntaxApplicativeId
import fs2.{Pipe, Stream}
import io.circe.Json
import model.{MALWARE, Packets, SOCIAL_ENGINEERING, UNWANTED_SOFTWARE}
import munit.CatsEffectSuite
import org.http4s.Method.GET
import org.http4s.Status.Ok
import org.http4s.circe.jsonEncoder
import org.http4s.client.*
import org.http4s.client.dsl.io.*
import org.http4s.dsl.io.*
import org.http4s.ember.client.*
import org.http4s.headers.*
import org.http4s.implicits.*
import org.http4s.{EntityEncoder, HttpApp, HttpRoutes, Request, Response, Status}
import server.processors.WebRiskProcessor
import server.processors.WebRiskProcessor.*
import server.Fixtures.{expectedPackets, validResponse}
import server.streams.webrisk.MockClient
import server.webrisk.WebRisk.*

import java.time.Duration
import java.util.{Calendar, Date}
import scala.concurrent.duration.{DurationInt, FiniteDuration}
import scala.util.chaining.scalaUtilChainingOps

class WebRiskProcessorSpec extends CatsEffectSuite:

  override val munitTimeout: FiniteDuration = 1.second

  val client: Resource[IO, Client[IO]] = MockClient.clientWithResponse(Ok, validResponse)

  val validRequest: Request[IO] = GET(uri"https://webrisk-mock-url.com")

  val delayedProcessor: Pipe[IO, Packets, Packets] = WebRiskProcessor
    .process(packet =>
      IO.sleep(500.millis) >>
        runSearchUriRequest(client, validRequest).map(threats =>
          packet.copy(threatTypes = threats)
        )
    )

  test("WebRisk processor runs concurrently") {
    val result = Stream
      .eval(IO.delay(expectedPackets.take(2)))
      .through(delayedProcessor)
      .compile
      .lastOrError

    assertIO(
      result,
      expectedPackets
        .take(2)
        .map(_.copy(threatTypes = Vector(SOCIAL_ENGINEERING, MALWARE, UNWANTED_SOFTWARE)))
    )
  }
