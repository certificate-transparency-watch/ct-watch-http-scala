package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class ApiSpec extends Specification with Specs2RouteTest {
  val api = new Api(system, new LogServerRepository)
  
  "API" should {

    "does domain stuff" in {
      Get("/domain/example.com") ~> api.route ~> check {
        responseAs[String] must contain("example.com")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> api.route ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the domain" in {
      Put("/domain/example.com") ~> api.sealRoute(api.route) ~> check {
        status === MethodNotAllowed
      }
    }
    
    "return log server with id" in {
      Get("/logserver/5") ~> api.route ~> check {
        responseAs[String] === "5 logServer5"
      }
    }
  }
}
