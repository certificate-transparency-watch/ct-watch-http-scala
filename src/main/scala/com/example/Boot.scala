package com.example

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Boot extends App {

  val system = ActorSystem("ct-watch-http")
  
  val api = new Api(new LogServerRepository)
  
  val service = system.actorOf(Props(new ApiActor(api)), "api")

  IO(Http)(system) ! Http.Bind(service, interface = "localhost", port = 8088)
}