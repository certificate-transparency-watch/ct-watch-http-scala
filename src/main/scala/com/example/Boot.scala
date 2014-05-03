package com.example

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import net.noerd.prequel.DatabaseConfig

object Boot extends App {

  val system = ActorSystem("ct-watch-http")
  
  val database = DatabaseConfig(
    driver = "org.postgresql.Driver",
    jdbcURL = "jdbc:postgresql://172.17.42.1/ct-watch?user=docker&password=docker"
  )
  
  val api = new Api(new LogServerRepository, new PostgresSignedTreeHeadRepository(database))
  
  val service = system.actorOf(Props(new ApiActor(api)), "api")

  IO(Http)(system) ! Http.Bind(service, interface = "localhost", port = 8088)
}