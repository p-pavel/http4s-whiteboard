package com.perikov.osgi.http4s.whiteboard.server

import org.osgi.service.component.annotations.*
import com.perikov.osgi.http4s.whiteboard.*

@Component(enabled = false)
class SampleRoute extends Http4sIORoutesProvider:
  import dsl.*
  lazy val routes: HttpRoutes[IO] = HttpRoutes.of {
    case GET -> _ => (
      IO.realTimeInstant flatMap (t => Ok(s"Current time is $t"))
    )
  }
