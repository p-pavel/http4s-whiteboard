# http4s-whiteboard

Whiteboard pattern for hosting http4s server in [OSGi](https://docs.osgi.org/specification/) container.

The project is in early stage. 

Here's the simplistic implementation of [HTTP Whiteboard](https://docs.osgi.org/specification/osgi.cmpn/8.0.0/service.http.whiteboard.html) idea using [http4s](https://http4s.org)

## Motivation

I like deploying to OSGi containers. This is how JVM based microservices should look like, not like tones of docker images each containing its own JVM running in K8s and communicating via snail-fast network RPC.

The container is observable, controllable, dynamic system. When you replace one service everything else keeps running. You got the hot JVM with all the code it pre-compiled and all the optimisation it made so far.

Each JVM gets enough CPU power to do its magic, not "0.2 vCPU", you don't pay the tax in memory for running every instance of JVM.

The downside is: if you get OOM the whole container goes down.

The solution is extremely simple though: do not get OOM.d

JVM is designed to work for prolonged periods of time and it's fantastic in doing so.

Combined with [cats-effect](https://typelevel.org/cats-effect/) asynchronous runtime we get extremely good performance and CPU utilization.

## Running

The project needs Scala libraries hosted in OSGi container to run (you can find [Karaf feature repository here](https://raw.githubusercontent.com/p-pavel/osgi-experiments/main/features.xml)).

The project uses [declarative services](https://docs.osgi.org/specification/osgi.cmpn/8.0.0/service.component.html). In Karaf you can install them with `feature:install scr`

## Future work
