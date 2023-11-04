package com.perikov.osgi.http4s.whiteboard.server;

import org.osgi.service.component.annotations.*;

@ComponentPropertyType
@interface Config {
  int port() default 8801;
}
