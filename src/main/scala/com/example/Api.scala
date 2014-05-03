package com.example

import akka.actor.{Actor, ActorRefFactory}
import spray.routing._
import spray.http._
import MediaTypes._
import Directives._
import spray.json._
import com.codahale.metrics.health.HealthCheckRegistry
import com.codahale.metrics.health.HealthCheck


class ApiActor(api : Api) extends HttpServiceActor {
  def receive = runRoute(api.route)
}

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val sthFormat = jsonFormat6(SignedTreeHead)
  
  implicit object HealthCheckResultFormat extends RootJsonFormat[HealthCheck.Result] {
    def write(r: HealthCheck.Result) = JsString(r.toString)
    def read(value: JsValue) = deserializationError("Unimplemented")
  }
}

class Api(logServerRepository: LogServerRepository, sthRepository : SignedTreeHeadRepository, healthCheckRegistry: HealthCheckRegistry) {
  
  import MyJsonProtocol._

  val route =
    path ("logserver" / Rest) { logServer =>
      get {
        complete {
          sthRepository.findByLogServerName(logServer).map { xs =>
            val (good, bad) = xs.partition(_.verified)
            Map("good" -> good, "bad" -> bad).toJson.prettyPrint              
          }
          
        }
      }
    } ~
    path ("domain" / Rest) { domain =>
      get {
        complete {
          domain
        }
      }
    } ~
    path ("health") {
      get {
        ctx => {
          import collection.JavaConverters._
          val results = healthCheckRegistry.runHealthChecks().asScala
          if (results.isEmpty)
            ctx.complete(StatusCodes.NotImplemented)
          else if (!results.filter { case (_, v) => !v.isHealthy }.isEmpty) {
            ctx.complete(500, results.toMap.toJson.prettyPrint)
          } else {
            ctx.complete(200, results.toMap.toJson.prettyPrint)
          }
        }
      }
    }
    
}