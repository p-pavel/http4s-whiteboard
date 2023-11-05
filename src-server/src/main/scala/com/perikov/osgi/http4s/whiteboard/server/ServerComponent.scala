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
  service = Array(classOf[ProviderRegistry]),
  property = Array("port:Short=8081"),
  reference = Array(
    new Reference(
      name = "providers",
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
class ServerComponent private (serverAndStop: (Ref[IO, RouteStore], IO[Unit]))
    extends ProviderRegistry,
      Bindings[ju.Map[String, ?], Unit]:

  private def stop: IO[Unit] = serverAndStop._2
  private def storeRef: Ref[IO, RouteStore] = serverAndStop._1

  override def iterator: Iterator[ProviderInfo] =
    storeRef.get.map(_.iterator).unsafeRunSync()

  @Activate
  def this(
      cfg: Config
  ) =
    this(
      Utils
        .routeStoreResource(toHostUnsafe(cfg.host()), toPortUnsafe(cfg.port()))(
          using mkLogger // TODO: deside about logger
        )
        .allocated.unsafeRunSync()
    )

  @Deactivate
  def deactivate = stop.unsafeRunSync()

  override def routeBind(
      r: Http4sIORoutesProvider,
      jProps: ju.Map[String, ?]
  ): Unit =
    val props = jProps.asScala.toMap
    val path = props
      .get("path")
      .map(_.toString)
      .getOrElse("/") // TODO: don't use default, correct README

    storeRef
      .update(_.routeBind(r, ProviderInfo(path, props)))
      .unsafeRunSync()

  override def routeUnbind(r: Http4sIORoutesProvider): Unit =
    storeRef.update(_.routeUnbind(r)).unsafeRunSync()
