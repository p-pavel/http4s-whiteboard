package com.perikov.osgi.http4s.whiteboard.server

import com.perikov.osgi.http4s.whiteboard.Http4sIORoutesProvider

/** Combines the abstraction of multiple [[Http4sIORoutesProvider]] into single
  * [[HttpRoutes]] an also acts as [[ProviderRegistry]]
  */
class RouteStore(m: Map[Http4sIORoutesProvider, ProviderInfo] = Map.empty)
    extends ProviderRegistry,
      Bindings[ProviderInfo, RouteStore],
      Http4sIORoutesProvider:

  import org.http4s.server.Router

  override def iterator: Iterator[ProviderInfo] = m.values.iterator

  val routes: HttpRoutes[IO] = Router(
    m.toSeq.map((p, info) => (info.path, p.routes))*
  )
  def routeBind(r: Http4sIORoutesProvider, info: ProviderInfo): RouteStore =
    RouteStore(m + (r -> info))

  def routeUnbind(r: Http4sIORoutesProvider): RouteStore = RouteStore(m - r)
end RouteStore
