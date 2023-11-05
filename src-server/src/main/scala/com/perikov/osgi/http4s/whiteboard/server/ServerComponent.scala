package com.perikov.osgi.http4s.whiteboard.server

import com.perikov.osgi.http4s.whiteboard.Http4sIORoutesProvider

import org.osgi
import osgi.service.component as scr
import scr.annotations.*

import cats.*
import cats.effect.*
import cats.effect.unsafe.implicits.global

import java.util as ju
import scala.jdk.CollectionConverters.*

import Utils.*

/** OSGi facing component for http4s server */
@Component(
  reference = Array(
    new Reference(
      name = "Funcs",
      service = classOf[Http4sIORoutesProvider],
      cardinality = ReferenceCardinality.MULTIPLE,
      policy = ReferencePolicy.DYNAMIC,
      bind = "routeBind",
      updated = "routeBind",
      unbind = "routeUnbind"
    )
  )
)
@Config
class ServerComponent private (serverAndStop: (ServerImpl, IO[Unit])):
  private def stop = serverAndStop._2
  private def server = serverAndStop._1

  @Activate
  def this(
      cfg: Config
  ) =
    this(
      ServerImpl
        .resource(toHostUnsafe(cfg.host()), toPortUnsafe(cfg.port()))(using
          mkLogger
        )
        .allocated
        .unsafeRunSync()
    )

  @Deactivate
  def deactivate = stop.unsafeRunSync()

  def routeBind(r: Http4sIORoutesProvider, props: ju.Map[String, ?]) =
    val path = props.asScala.get("path").map(_.toString).getOrElse("/")
    server
      .routeBind(r, path)
      .unsafeRunAndForget()

  def routeUnbind(r: Http4sIORoutesProvider) =
    server.routeUnbind(r).unsafeRunAndForget()
