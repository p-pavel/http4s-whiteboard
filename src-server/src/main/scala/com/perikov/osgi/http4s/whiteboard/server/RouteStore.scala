package com.perikov.osgi.http4s.whiteboard.server

import com.perikov.osgi.http4s.whiteboard.Http4sIORoutesProvider

class RouteStore(m: Map[Http4sIORoutesProvider, String] = Map.empty)
    extends Bindings[String, RouteStore],
      Http4sIORoutesProvider:

  import org.http4s.server.Router

  val routes: HttpRoutes[IO] = Router(
    m.toSeq.map((p, path) => (path, p.routes))*
  )
  def routeBind(r: Http4sIORoutesProvider, path: String): RouteStore =
    RouteStore(m + (r -> path))

  def routeUnbind(r: Http4sIORoutesProvider): RouteStore =
    RouteStore(m - r)
end RouteStore