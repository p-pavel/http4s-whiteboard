package com.perikov.osgi.http4s.whiteboard.server

import com.perikov.osgi.http4s.whiteboard.Http4sIORoutesProvider

import org.http4s
import http4s.HttpRoutes

case class ProviderInfo(path: String, props: Map[String, ?])

/** Named variant of [[Iterable]] intended to be exposed as OSGi service */
trait ProviderRegistry extends Iterable[ProviderInfo]

/** an abstraction of operations for adding and removing
  * [[Http4sIORoutesProvicer]]
  */
trait Bindings[Info, Res]:
  def routeBind(r: Http4sIORoutesProvider, info: Info): Res
  def routeUnbind(r: Http4sIORoutesProvider): Res

object Utils:
  import org.typelevel.log4cats
  import log4cats.slf4j.*
  import log4cats.*
  import cats.effect.*
  import cats.effect.unsafe.implicits.global
  import com.comcast.ip4s.{Port, Host}
  import cats.implicits.*
  import cats.data.{Kleisli, OptionT}
  import http4s.ember.server.EmberServerBuilder

  def mkLogger: Logger[IO] = Slf4jLogger
    .fromName[IO]("sdfsa")
    .unsafeRunSync()

  private def getUnsafe[A, T](msg: => String, convert: A => Option[T])(
      arg: A
  ): T =
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

  private def flattenRoutes(r: IO[HttpRoutes[IO]]): HttpRoutes[IO] =
    val t: OptionT[IO, HttpRoutes[IO]] = OptionT(r.map(_.some))
    Kleisli(req => t.flatMap(_.run(req)))

  private def routesFrom(ref: Ref[IO, RouteStore]): HttpRoutes[IO] =
    Utils.flattenRoutes(ref.get.map(_.routes))

  def routeStoreResource(host: Host, port: Port)(using logger: Logger[IO]): Resource[IO, Ref[IO, RouteStore]] =
    Resource
      .eval(Ref[IO].of(RouteStore()))
      .flatTap(r => serverResource(host, port, routesFrom(r)))

  private def serverResource(host: Host, port: Port, routes: HttpRoutes[IO]) =
    EmberServerBuilder
      .default[IO]
      .withHost(host)
      .withHttpApp(routes.orNotFound)
      .withPort(port)
      .build

end Utils
