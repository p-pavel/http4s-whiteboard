package com.perikov.osgi.http4s.whiteboard.server

import com.perikov.osgi.http4s.whiteboard.Http4sIORoutesProvider

trait Bindings[Path,Res]:
  def routeBind(r: Http4sIORoutesProvider, path: Path): Res
  def routeUnbind(r: Http4sIORoutesProvider): Res