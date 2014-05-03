package com.example

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http

object Boot extends App {

  val system = ActorSystem("on-spray-can")
  
  val api = new Api(new LogServerRepository)
  
  val service = system.actorOf(Props(new ApiActor(api)), "demo-service")

  IO(Http)(system) ! Http.Bind(service, interface = "localhost", port = 8088)
}