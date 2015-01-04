package com.example

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import net.noerd.prequel.DatabaseConfig
import com.codahale.metrics.health.HealthCheckRegistry

object Boot extends App {

  val system = ActorSystem("ct-watch-http")
  
  val database = DatabaseConfig(
    driver = "org.postgresql.Driver",
    jdbcURL = "jdbc:postgresql://172.17.42.1/ct-watch?user=docker&password=docker"
  )
  
  val healthCheckRegistry = new HealthCheckRegistry
  
  val api = new Api(new LogServerRepository, new PostgresSignedTreeHeadRepository(database), healthCheckRegistry, new PostgresLogEntryRepository(database))
  
  new HealthChecks(healthCheckRegistry, database)
  
  val service = system.actorOf(Props(new ApiActor(api)), "api")

  IO(Http)(system) ! Http.Bind(service, interface = "0.0.0.0", port = 8088)
}