package com.perikov.osgi.http4s.whiteboard

import cats.effect.IO
import cats.data.OptionT
import org.http4s.{Response, Request}
import org.osgi.annotation.versioning.ProviderType

/** OSGi service interface 
 * @todo properties for servers where to register
*/
@ProviderType
trait Http4sIORoute extends (Request[IO] => OptionT[IO,Response[IO]])
