package com.tinylabproductions.akka_http_daemon

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.Logger

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.control.NonFatal

/**
  * Created by arturas on 2016-12-29.
  */
object AkkaHttpDaemon {
  case class HttpConfig(host: String, port: Int) {
    val url = s"http://$host:$port/"
  }

  case class Config(http: HttpConfig)
}
class AkkaHttpDaemon(
  conf: AkkaHttpDaemon.Config,
  handler: Route,
  afterBind: () => Unit,
  afterUnbind: () => Unit
)(implicit log: Logger, system: ActorSystem) {
  implicit val actorMaterializer = ActorMaterializer()(system)
  import system.dispatcher

  log.debug(s"Binding HTTP server to ${conf.http.url}")
  val exceptionHandled = extractUri { uri =>
    handleExceptions(ExceptionHandler {
      case NonFatal(t) =>
        log.error(s"Error while handling '$uri'", t)
        complete(HttpResponse(StatusCodes.InternalServerError))
    })(handler)
  }

  val bindingFuture = Http()(system).bindAndHandle(exceptionHandled, conf.http.host, conf.http.port)

  bindingFuture.onComplete {
    case util.Success(binding) =>
      log.info(s"Server online at ${conf.http.url}")

      log.debug("Attaching shutdown hook.")
      // Scala 2.11.x compatibility.
      val runnable = new Runnable { override def run() = {
        log.info("Shutting down...")

        val unbindF = binding.unbind()
        log.info("Waiting for unbind.")
        Await.result(unbindF, Duration.Inf)
        log.info("Unbind complete.")
        afterUnbind()
        log.info("Terminating system.")
        val terminateF = system.terminate()
        log.info("Waiting for system termination.")
        Await.ready(terminateF, Duration.Inf)
        log.info("System terminated, shut down.")
      } }
      Runtime.getRuntime.addShutdownHook(new Thread(runnable))

      afterBind()

      Await.result(system.whenTerminated, Duration.Inf)
    case util.Failure(t) =>
      log.error(s"Binding to ${conf.http.url} failed, shutting down", t)
      system.terminate().onComplete(_ => System.exit(1))
  }
}
