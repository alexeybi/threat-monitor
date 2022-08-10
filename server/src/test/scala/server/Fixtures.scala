package server

import cats.effect.IO
import model.Packet

import scala.util.chaining.scalaUtilChainingOps

object Fixtures:

  val rawPackets: IO[String] = s"""
    |www.gstatic.com,,2a00:1450:4001:829::2003,2022-07-22T16:04:28.025981
    |gstatic.com,,           2a00:1450:4001:801::2003,2022-07-22T16:04:28.025981
    |safebrowsing.brave.com,35.84.97.53,,2022-07-22T16:04:28.025981
    |safebrowsing.brave.com,35.84.97.53,,2022-07-22T16:04:28.025981
    |
    |  http4s.org,185.199.111.153,,2022-07-22T16:04:28.025981
    | "",,,
    |,192.168.178.20,,2022-07-22T16:04:28.025981
    |          safebrowsing.brave.com,   35.84.97.53, ,   2022-07-22T16:04:28.025981
    |,192.168.178.20,,
    |play.google.com,,2a00:1450:4001:80b::200e,2022-07-22T16:04:28.025981
    |*** tshark error message ***""".stripMargin pipe IO.delay

  val expectedPackets: Vector[Packet] = Vector(
    Packet(
      "www.gstatic.com",
      "2a00:1450:4001:829::2003",
      "2022-07-22T16:04:28.025981",
      Vector.empty
    ),
    Packet(
      "gstatic.com",
      "2a00:1450:4001:801::2003",
      "2022-07-22T16:04:28.025981",
      Vector.empty
    ),
    Packet(
      "safebrowsing.brave.com",
      "35.84.97.53",
      "2022-07-22T16:04:28.025981",
      Vector.empty
    ),
    Packet("http4s.org", "185.199.111.153", "2022-07-22T16:04:28.025981", Vector.empty),
    Packet(
      "play.google.com",
      "2a00:1450:4001:80b::200e",
      "2022-07-22T16:04:28.025981",
      Vector.empty
    )
  )

  val validResponse =
    """{
      "threat": {
        "threatTypes": [
          "SOCIAL_ENGINEERING", "MALWARE", "UNWANTED_SOFTWARE"
        ],
        "expireTime": "2022-08-02T17:43:14.878855797Z"
      }
    }"""
