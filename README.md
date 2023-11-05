# http4s-whiteboard

Whiteboard pattern for hosting http4s server in [OSGi](https://docs.osgi.org/specification/) container.

Here's the simplistic implementation of [HTTP Whiteboard](https://docs.osgi.org/specification/osgi.cmpn/8.0.0/service.http.whiteboard.html) idea using [http4s](https://http4s.org)

Feel free to share your ideas and discuss features at [gitter](https://app.element.io/#/room/#http4s-whiteboard:gitter.im)

**BTW: as of November 2023 I'm actively looking for the Scala job. Please contact me if you have any proposals.**

Also, you can 

<a href="https://www.buymeacoffee.com/perikov" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-blue.png" alt="Buy Me A Beer" style="height: 60px !important;width: 217px !important;" ></a>


## Motivation

I like deploying to OSGi containers. This is how JVM based microservices should look like, not like tones of docker images each containing its own JVM running in K8s and communicating via snail-fast network RPC.

OSGi container is an observable, controllable, dynamic system. When you replace one service everything else keeps running and all dependencies are automatically updated in container. You keep the hot JVM with all the code it pre-compiled and all the optimisation it made so far.

Each JVM gets enough CPU power to do its magic, not "0.2 vCPU" and you don't have to pay the tax in memory for running separate instance of JVM for every microservice.

The downside is: if you get OOM-killed the whole container goes down.

The solution is extremely simple though: do not become OOM.

JVM is designed to work for prolonged periods of time and it's fantastic in doing so.

Combined with [cats-effect](https://typelevel.org/cats-effect/) asynchronous runtime we get extremely good performance and CPU utilization.

## Running

See the example http4s routes provider [here](./blob/main/src/main/scala/com/perikov/osgi/http4s/whiteboard/server/SampleRoutes.scala)

```sh
karaf
karaf@root()> feature:repo-add https://raw.githubusercontent.com/p-pavel/osgi-experiments/main/features.xml # Scala libraries
karaf@root()> feature:repo-add https://raw.githubusercontent.com/p-pavel/http4s-whiteboard/feature.xml # repo containing http4s-whiteboard feature
karaf@root()> feature:install com.perikov.http4s-whiteboard
karaf@root()> scr:list
# ServiceComponentRuntimeMBean in bundle 53 (org.apache.karaf.scr.management:4.4.4) enabled, 1 instance.
#    Id: 0, State:ACTIVE
# ServiceComponentRuntimeBundleStateService in bundle 54 (org.apache.karaf.scr.state:4.4.4) enabled, 1 instance.
#    Id: 1, State:ACTIVE
# com.perikov.osgi.http4s.whiteboard.server.Server in bundle 56 (com.perikov.http4s.whiteboard:0.1.0.SNAPSHOT) enabled, 1 instance.
#    Id: 2, State:ACTIVE
# com.perikov.osgi.http4s.whiteboard.server.SampleRoute in bundle 56 (com.perikov.http4s.whiteboard:0.1.0.SNAPSHOT) disabled, 0 instances.
karaf@root()> scr:enable SampleRoute
karaf@root()> http4s:list
# path  │ service.bundleid │ service.id │ component.name
# ──────┼──────────────────┼────────────┼──────────────────────────────────────────────────────
# /time │ 84               │ 109        │ com.perikov.osgi.http4s.whiteboard.server.SampleRoute
```

Now you server is running on port 8801.

You can change the port via configuration (container will restart the server automagically):

```sh
karaf@root()> config:property-set -p  com.perikov.osgi.http4s.whiteboard.server.Server port 8802
```

The project needs Scala libraries hosted in OSGi container to run (you can find [Karaf feature repository here](https://raw.githubusercontent.com/p-pavel/osgi-experiments/main/features.xml)).

The project uses [declarative services](https://docs.osgi.org/specification/osgi.cmpn/8.0.0/service.component.html). In Karaf you can install them with `feature:install scr`

## Future work
