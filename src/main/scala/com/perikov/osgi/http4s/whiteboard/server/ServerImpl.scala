package com.perikov.osgi.http4s.whiteboard.server

import com.perikov.osgi.http4s.whiteboard.Http4sIORoute
import cats.*
import cats.effect.*
import com.comcast.ip4s.*

import org.typelevel.log4cats
import log4cats.Logger
import log4cats.syntax.*

import org.http4s.ember.server.EmberServerBuilder

trait ServerImpl:
  def routeBind(r: Http4sIORoute): IO[Unit]
  def routeUnbind(r: Http4sIORoute): IO[Unit]

object ServerImpl:
  import cats.effect.*
  import cats.data.{OptionT,Kleisli}
  import org.http4s
  import http4s.*
  import cats.implicits.*

  type RouteMap = Set[Http4sIORoute]

  private class Data(
      val routeRef: Ref[IO, RouteMap]
  )(using Logger[IO]):
    private def combine(k: Iterable[HttpRoutes[IO]]): HttpRoutes[IO] = 
      k.fold(HttpRoutes.empty[IO])(_ <+> _)

    private def fl(arg: IO[HttpRoutes[IO]]): HttpRoutes[IO] = 
      Kleisli( r => OptionT(arg.flatMap(_.run(r).value)) )
      
    val routes: HttpRoutes[IO] =
      fl(routeRef.get.map(m => combine(m.map(Kleisli(_)))))
    
    def modifyMap(msg: String, op: RouteMap=>RouteMap):IO[Unit] = 
      routeRef.modify{s => 
        val oldSize = s.size
        val newS = op(s)
        val newSize = newS.size
        (newS, (oldSize,newSize))
        }.flatMap((oldSize, newSize) => info"$msg. Size: $oldSize -> $newSize")

  private def emptyMap: RouteMap = Set.empty

  //TODO race conditions when adding and removing routes
  def resource(port: Port)(using logger: Logger[IO]): Resource[IO, ServerImpl] =
    def createData: IO[Data] = Ref[IO].of(emptyMap).map(Data(_))
    for
      data <- Resource.eval(createData)
      server <- EmberServerBuilder
        .default[IO]
        .withHost(host"0.0.0.0")
        .withHttpApp(data.routes.orNotFound)
        .withPort(port)
        .build
    yield
      new:

        override def routeBind(r: Http4sIORoute): IO[Unit] =
          data.modifyMap("Adding route", _ + r)
        override def routeUnbind(r: Http4sIORoute): IO[Unit] =
          data.modifyMap("Removing route", _ - r)
      end new
