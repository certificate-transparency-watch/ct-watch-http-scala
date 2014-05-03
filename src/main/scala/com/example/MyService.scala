package com.example

import akka.actor.{Actor, ActorRefFactory}
import spray.routing._
import spray.http._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor(myService : MyService) extends Actor {

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = myService.runRoute(myService.myRoute)
}

class LogServerRepository {
  def lookup(logServerId: Int) : LogServer = LogServer(logServerId, "logServer" + logServerId)
}

case class LogServer(id: Int, name: String)


// this trait defines our service behavior independently from the service actor
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