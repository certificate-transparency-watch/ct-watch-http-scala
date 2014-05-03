package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class MyServiceSpec extends Specification with Specs2RouteTest {
  val myService = new MyService(system, new LogServerRepository)
  
  "MyService" should {

    "does domain stuff" in {
      Get("/domain/example.com") ~> myService.myRoute ~> check {
        responseAs[String] must contain("example.com")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myService.myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the domain" in {
      Put("/domain/example.com") ~> myService.sealRoute(myService.myRoute) ~> check {
        status === MethodNotAllowed
      }
    }
    
    "return log server with id" in {
      Get("/logserver/5") ~> myService.myRoute ~> check {
        responseAs[String] === "5 logServer5"
      }
    }
  }
}
