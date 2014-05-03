package com.example

import akka.actor.{Actor, ActorRefFactory}
import spray.routing._
import spray.http._
import MediaTypes._
import Directives._
import spray.json._


class ApiActor(api : Api) extends HttpServiceActor {
  def receive = runRoute(api.route)
}

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat6(SignedTreeHead)
}

class Api(logServerRepository: LogServerRepository, sthRepository : SignedTreeHeadRepository) {
  
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
    }
    
}