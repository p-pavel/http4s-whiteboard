package com.perikov.osgi.http4s.whiteboard.server;

import org.osgi.service.component.annotations.*;

@ComponentPropertyType
@interface Config {
  int port() default 8801;
  String host() default "0.0.0.0";
}
