package com.perikov.osgi.http4s.whiteboard.server

import com.perikov.osgi.http4s.whiteboard.Http4sIORoute

import org.osgi
import osgi.service.component as scr
import scr.annotations.*

import cats.*
import cats.implicits.*
import cats.effect.*
import cats.effect.unsafe.implicits.global
import com.comcast.ip4s.*

import org.typelevel.log4cats
import org.osgi.service.component.ComponentContext


/** OSGi facing component for http4s server */
@Component(
  reference = Array(
    new Reference(
      name = "Funcs",
      service = classOf[Http4sIORoute],
      cardinality = ReferenceCardinality.MULTIPLE,
      policy = ReferencePolicy.DYNAMIC,
      bind = "routeBind",
      unbind = "routeUnbind"
    )
  )
)
@Config
class Server private (serverAndStop: (ServerImpl, IO[Unit])):
  private def stop = serverAndStop._2
  private def server = serverAndStop._1

  @Activate
  def this(
      cfg: Config,
      ctx: ComponentContext

  ) =
    this(
      ServerImpl
        .resource(cfg.port().toPortUnsafe)(using
          log4cats.slf4j.Slf4jLogger
            .fromName[IO]("sdfsa")
            .unsafeRunSync()
        )
        .allocated
        .unsafeRunSync()
    )

  @Deactivate
  def deactivate = stop.unsafeRunSync()

  def routeBind(r: Http4sIORoute) =
    server.routeBind(r).unsafeRunAndForget()

  def routeUnbind(r: Http4sIORoute) =
    server.routeUnbind(r).unsafeRunAndForget()

extension (n: Int)
  @`throws`[IllegalArgumentException]
  def toPortUnsafe: Port =
    Port
      .fromInt(n)
      .toRight(s"Invalid IP port $n")
      .leftMap(IllegalArgumentException(_))
      .toTry
      .get
