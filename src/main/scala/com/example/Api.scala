package com.example

import akka.actor.{Actor, ActorRefFactory}
import spray.routing._
import spray.http._
import MediaTypes._

class ApiActor(api : Api) extends Actor {
  def receive = api.runRoute(api.route)
}

class Api(val actorRefFactory : ActorRefFactory, logServerRepository: LogServerRepository) extends HttpService {

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