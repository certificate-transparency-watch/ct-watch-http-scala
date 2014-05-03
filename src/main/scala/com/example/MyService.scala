package com.example

import akka.actor.{Actor, ActorRefFactory}
import spray.routing._
import spray.http._
import MediaTypes._

class MyServiceActor(myService : MyService) extends Actor {
  def receive = myService.runRoute(myService.myRoute)
}

class LogServerRepository {
  def lookup(logServerId: Int) : LogServer = LogServer(logServerId, "logServer" + logServerId)
}

case class LogServer(id: Int, name: String)


class MyService(val actorRefFactory : ActorRefFactory, logServerRepository: LogServerRepository) extends HttpService {

  val myRoute =
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