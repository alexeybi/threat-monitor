package server.streams.webrisk

import cats.effect.{IO, Resource}
import cats.implicits.catsSyntaxApplicativeId
import io.circe.{DecodingFailure, Json}
import io.circe.parser.*
import model.{MALWARE, SOCIAL_ENGINEERING, ThreatType, UNWANTED_SOFTWARE, given}
import munit.CatsEffectSuite
import org.http4s.Method.GET
import org.http4s.Status.{Ok, Unauthorized}
import org.http4s.circe.*
import org.http4s.client.dsl.io.*
import org.http4s.implicits.*
import org.http4s.{EntityEncoder, HttpApp, Request, Response, Status}
import server.*
import server.Fixtures.validResponse
import server.streams.webrisk.MockClient.*
import server.webrisk.WebRisk.*

import scala.util.chaining.scalaUtilChainingOps

class WebRiskSpec extends CatsEffectSuite:

  val request: Request[IO] = GET(uri"https://webrisk-mock-url.com")

  test("Request returns list of threat types") {

    val client = clientWithResponse(Ok, validResponse)

    assertIO(
      runSearchUriRequest(client, request),
      Vector(SOCIAL_ENGINEERING, MALWARE, UNWANTED_SOFTWARE)
    )
  }

  test("Request returns empty object") {

    val client = clientWithResponse(Ok, "{}")

    assertIO(runSearchUriRequest(client, request), Vector())
  }

  test("Request returns error") {
    val client = clientWithResponse(Unauthorized, """{ "error": "FAIL" }""")
    interceptMessageIO[DecodingFailure](
      "Couldn't decode WebRisk response: DownField(threat)"
    )(runSearchUriRequest(client, request))
  }
