package com.perikov.osgi.http4s.whiteboard.server

import org.osgi.service.component.annotations.*
import com.perikov.osgi.http4s.whiteboard.*
import com.perikov.osgi.http4s.whiteboard.Http4sIORoutesProvider

@Component(
  enabled = false,
  property = Array("path:String=/")
)
class SampleRoute extends Http4sIORoutesProvider:
  import dsl.*
  val routes: HttpRoutes[IO] = HttpRoutes.of {
    case GET -> _ => (
      IO.realTimeInstant `flatMap` (t => Ok(s"Current time is $t"))
    )
  }
