package com.example

import akka.actor.{Actor, ActorRefFactory}
import spray.routing._
import spray.http._
import MediaTypes._
import Directives._

class ApiActor(api : Api) extends HttpServiceActor {
  def receive = runRoute(api.route)
}

class Api(logServerRepository: LogServerRepository) {

  val route =
    path ("logserver" / IntNumber) { logServerId =>
      get {
        complete {
          val ls = logServerRepository.lookup(logServerId)
          ls.id + " " + ls.name
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