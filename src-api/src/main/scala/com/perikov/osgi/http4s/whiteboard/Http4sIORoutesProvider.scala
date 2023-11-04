package com.perikov.osgi.http4s.whiteboard

import org.osgi.annotation.versioning.ProviderType


/** OSGi service interface
 * the service may provide the following properies:
  * - `path` (defaluts to "/")
  */
@ProviderType
trait Http4sIORoutesProvider: 
  import org.http4s
  import cats.effect
  // some types to make the life of implementers easier
  protected type IO[A] = effect.IO[A]
  protected inline def IO = effect.IO
  protected type HttpRoutes[F[_]] = http4s.HttpRoutes[F]
  protected inline def HttpRoutes = http4s.HttpRoutes
  protected type OptionT[F[_], A] = cats.data.OptionT[F, A]
  protected type Request[F[_]] = http4s.Request[F]
  protected type Response[F[_]] = http4s.Response[F]

  /** import `dsl.*` to bring [[org.http4s.dsl.io]] into scope */
  protected transparent inline def dsl = org.http4s.dsl.io

  val routes: HttpRoutes[IO]
