package com.example

import com.google.common.base.Charsets
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import spray.routing.HttpService
import spray.json._
import com.codahale.metrics.health.HealthCheckRegistry
import com.codahale.metrics.health.HealthCheck
import collection.JavaConverters._
import scala.collection.immutable.SortedMap
import java.util.TreeMap

class StubSignedTreeHeadRepository extends SignedTreeHeadRepository {
  override val findByLogServerName: String => Option[List[SignedTreeHead]] = {
    case "google"            => Some(List(SignedTreeHead(1,1337,"a","b",true,1), SignedTreeHead(2, 1338, "c", "d", false, 1)))
    case "noSignedTreeHeads" => Some(List())
    case _                   => None
  }
}

class StubHealthCheckRegistry extends HealthCheckRegistry {
  override def runHealthChecks() = {
    new TreeMap(SortedMap(
        "db" -> HealthCheck.Result.unhealthy("unconnectable"),
        "queue" -> HealthCheck.Result.healthy("all good")
    ).asJava)
  }
}

class StubLogEntryRepository extends LogEntryRepository {
  override def lookupByDomain(domain: String) = {
    val githubCert = "MIIF4DCCBMigAwIBAgIQDACTENIG2+M3VTWAEY3chzANBgkqhkiG9w0BAQsFADB1\nMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3\nd3cuZGlnaWNlcnQuY29tMTQwMgYDVQQDEytEaWdpQ2VydCBTSEEyIEV4dGVuZGVk\nIFZhbGlkYXRpb24gU2VydmVyIENBMB4XDTE0MDQwODAwMDAwMFoXDTE2MDQxMjEy\nMDAwMFowgfAxHTAbBgNVBA8MFFByaXZhdGUgT3JnYW5pemF0aW9uMRMwEQYLKwYB\nBAGCNzwCAQMTAlVTMRkwFwYLKwYBBAGCNzwCAQITCERlbGF3YXJlMRAwDgYDVQQF\nEwc1MTU3NTUwMRcwFQYDVQQJEw41NDggNHRoIFN0cmVldDEOMAwGA1UEERMFOTQx\nMDcxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1T\nYW4gRnJhbmNpc2NvMRUwEwYDVQQKEwxHaXRIdWIsIEluYy4xEzARBgNVBAMTCmdp\ndGh1Yi5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCx1Nw8r/3z\nTu3BZ63myyLot+KrKPL33GJwCNEMr9YWaiGwNksXDTZjBK6/6iBRlWVm8r+5TaQM\nKev1FbHoNbNwEJTVG1m0Jg/Wg1dZneF8Cd3gE8pNb0Obzc+HOhWnhd1mg+2TDP4r\nbTgceYiQz61YGC1R0cKj8keMbzgJubjvTJMLy4OUh+rgo7XZe5trD0P5yu6ADSin\ndvEl9ME1PPZ0rd5qM4J73P1LdqfC7vJqv6kkpl/nLnwO28N0c/p+xtjPYOs2ViG2\nwYq4JIJNeCS66R2hiqeHvmYlab++O3JuT+DkhSUIsZGJuNZ0ZXabLE9iH6H6Or6c\nJL+fyrDFwGeNAgMBAAGjggHuMIIB6jAfBgNVHSMEGDAWgBQ901Cl1qCt7vNKYApl\n0yHU+PjWDzAdBgNVHQ4EFgQUakOQfTuYFHJSlTqqKApD+FF+06YwJQYDVR0RBB4w\nHIIKZ2l0aHViLmNvbYIOd3d3LmdpdGh1Yi5jb20wDgYDVR0PAQH/BAQDAgWgMB0G\nA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjB1BgNVHR8EbjBsMDSgMqAwhi5o\ndHRwOi8vY3JsMy5kaWdpY2VydC5jb20vc2hhMi1ldi1zZXJ2ZXItZzEuY3JsMDSg\nMqAwhi5odHRwOi8vY3JsNC5kaWdpY2VydC5jb20vc2hhMi1ldi1zZXJ2ZXItZzEu\nY3JsMEIGA1UdIAQ7MDkwNwYJYIZIAYb9bAIBMCowKAYIKwYBBQUHAgEWHGh0dHBz\nOi8vd3d3LmRpZ2ljZXJ0LmNvbS9DUFMwgYgGCCsGAQUFBwEBBHwwejAkBggrBgEF\nBQcwAYYYaHR0cDovL29jc3AuZGlnaWNlcnQuY29tMFIGCCsGAQUFBzAChkZodHRw\nOi8vY2FjZXJ0cy5kaWdpY2VydC5jb20vRGlnaUNlcnRTSEEyRXh0ZW5kZWRWYWxp\nZGF0aW9uU2VydmVyQ0EuY3J0MAwGA1UdEwEB/wQCMAAwDQYJKoZIhvcNAQELBQAD\nggEBAG/nbcuC8++QhwnXDxUiLIz+06scipbbXRJd0XjAMbD/RciJ9wiYUhcfTEsg\nZGpt21DXEL5+q/4vgNipSlhBaYFyGQiDm5IQTmIte0ZwQ26jUxMf4pOmI1v3kj43\nFHU7uUskQS6lPUgND5nqHkKXxv6V2qtHmssrA9YNQMEK93ga2rWDpK21mUkgLviT\nPB5sPdE7IzprOCp+Ynpf3RcFddAkXb6NqJoQRPrStMrv19C1dqUmJRwIQdhkkqev\nff6IQDlhC8BIMKmCNK33cEYDfDWROtW7JNgBvBTwww8jO1gyug8SbGZ6bZ3k8OV8\nXX4C2NesiZcLYbc2n7B9O+63M2k="
    List(LogEntry("ct.googleapis.com/pilot", 100, "example.com", githubCert))
  }
}

class ApiSpec extends Specification with Specs2RouteTest with HttpService {
  def actorRefFactory = system
  
  val api = new Api(new LogServerRepository, new StubSignedTreeHeadRepository, new StubHealthCheckRegistry, new StubLogEntryRepository)
  
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

    "returns 404 if asking for whole .com" in {
      Get("/domain/com") ~> api.route ~> check {
        status === NotFound
      }
    }

    "returns 404 if asking for whole .co.uk" in {
      Get("/domain/co.uk") ~> api.route ~> check {
        status === NotFound
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
    
    "health checks returns 500 on failure" in {
      Get("/health") ~> api.route ~> check {
        status === InternalServerError
        responseAs[String] contains "unconnectable"
      }
    }
  }
}
