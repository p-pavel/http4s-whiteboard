package com.perikov.osgi.http4s.whiteboard.server

import org.http4s.HttpRoutes

object Utils:
  import org.typelevel.log4cats
  import log4cats.slf4j.*
  import log4cats.*
  import cats.effect.*
  import cats.effect.unsafe.implicits.global
  import com.comcast.ip4s.{Port, Host}
  import cats.implicits.*
  import cats.data.{Kleisli, OptionT}

  def mkLogger: Logger[IO] = Slf4jLogger
    .fromName[IO]("sdfsa")
    .unsafeRunSync()

  def getUnsafe[A, T](msg: => String, convert: A => Option[T])(arg: A): T =
    convert(arg)
      .toRight(msg)
      .leftMap(IllegalArgumentException(_))
      .toTry
      .get

  /** @throws [[IllegalArgumentException]] */
  def toPortUnsafe(n: Int): Port =
    getUnsafe(s"Invalid port $n", Port.fromInt)(n)

  /** @throws [[IllegalArgumentException]] */
  def toHostUnsafe(h: String): Host =
    getUnsafe(s"Invalid host '$h'", Host.fromString)(h)

  def flattenRoutes(r: IO[HttpRoutes[IO]]): HttpRoutes[IO] =
    val t: OptionT[IO, HttpRoutes[IO]] = OptionT(r.map(_.some))
    Kleisli(req => t.flatMap(_.run(req)))

end Utils
