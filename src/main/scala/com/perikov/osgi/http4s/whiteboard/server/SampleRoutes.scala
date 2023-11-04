package com.perikov.osgi.http4s.whiteboard.server
import org.osgi.service.component.annotations.*
import com.perikov.osgi.http4s.whiteboard.*

@Component(enabled = false)
class SampleRoute extends Http4sIORoute:
  import org.http4s.dsl.io.*
  import cats.effect.*

  import cats.data.OptionT
  import org.http4s.*

  val routes: HttpRoutes[IO] = HttpRoutes.of {
    case GET -> _ => (IO.realTimeInstant flatMap (t => Ok(s"Current time is $t")))
  }
  override def apply(v1: Request[IO]): OptionT[IO, Response[IO]] = routes.run(v1)
    
