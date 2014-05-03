package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import spray.routing.HttpService
import spray.json._

class StubSignedTreeHeadRepository extends SignedTreeHeadRepository {
  override val findByLogServerName: String => Option[List[SignedTreeHead]] = {
    case "google"            => Some(List(SignedTreeHead(1,1337,"a","b",true,1), SignedTreeHead(2, 1338, "c", "d", false, 1)))
    case "noSignedTreeHeads" => Some(List())
    case _                   => None
  }
}

class ApiSpec extends Specification with Specs2RouteTest with HttpService {
  def actorRefFactory = system
  
  val api = new Api(new LogServerRepository, new StubSignedTreeHeadRepository)
  
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
      Put("/domain/example.com") ~> sealRoute(api.route) ~> check {
        status === MethodNotAllowed
      }
    }
    
    "return log server with id" in {
      Get("/logserver/google") ~> api.route ~> check {
        responseAs[String] contains "1337"
      }
    }
    
    "returns empty for empty" in {
      Get("/logserver/noSignedTreeHeads") ~> api.route ~> check {
        responseAs[String].parseJson === """{"good":[], "bad":[]}""".parseJson
      }
    }
    
    "returns 404 error when log server doesn't exist" in {
      Get("/logserver/nope") ~> api.route ~> check {
        status === NotFound
      }
    }
  }
}
