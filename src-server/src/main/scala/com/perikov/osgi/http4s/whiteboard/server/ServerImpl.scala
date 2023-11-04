package com.perikov.osgi.http4s.whiteboard.server

import com.perikov.osgi.http4s.whiteboard.Http4sIORoutesProvider
import cats.effect.{IO, Ref}

class ServerImpl(ref: Ref[IO, RouteStore]) extends Bindings[String, IO[Unit]]:
  override def routeBind(r: Http4sIORoutesProvider, path: String): IO[Unit] =
    ref.update(_.routeBind(r, path))
  override def routeUnbind(r: Http4sIORoutesProvider): IO[Unit] =
    ref.update(_.routeUnbind(r))

object ServerImpl:
  import cats.effect.Resource
  import org.http4s
  import http4s.HttpRoutes
  import http4s.ember.server.EmberServerBuilder
  import com.comcast.ip4s.{Host, Port}
  import org.typelevel.log4cats.Logger

  private def routes(ref: Ref[IO, RouteStore]): HttpRoutes[IO] =
    Utils.flattenRoutes(ref.get.map(_.routes))

  private def serverResource(host: Host, port: Port, routes: HttpRoutes[IO]) =
    EmberServerBuilder
      .default[IO]
      .withHost(host)
      .withHttpApp(routes.orNotFound)
      .withPort(port)
      .build

  def resource(host: Host, port: Port)(using
      logger: Logger[IO]
  ): Resource[IO, ServerImpl] =
    for
      ref <- Resource.eval(Ref[IO].of(RouteStore()))
      _ <- serverResource(host, port, routes(ref))
    yield ServerImpl(ref)
