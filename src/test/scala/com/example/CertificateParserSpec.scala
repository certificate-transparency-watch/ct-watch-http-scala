package com.example

import com.google.common.base.Charsets
import org.joda.time.{DateTimeZone, DateTime}
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest

class CertificateParserSpec extends Specification with Specs2RouteTest {

  val certificateParser = new CertificateParser

  "CertificateParser" should {
    "parse a github.com cert" in {
      val githubCert = "-----BEGIN CERTIFICATE-----\nMIIF4DCCBMigAwIBAgIQDACTENIG2+M3VTWAEY3chzANBgkqhkiG9w0BAQsFADB1\nMQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3\nd3cuZGlnaWNlcnQuY29tMTQwMgYDVQQDEytEaWdpQ2VydCBTSEEyIEV4dGVuZGVk\nIFZhbGlkYXRpb24gU2VydmVyIENBMB4XDTE0MDQwODAwMDAwMFoXDTE2MDQxMjEy\nMDAwMFowgfAxHTAbBgNVBA8MFFByaXZhdGUgT3JnYW5pemF0aW9uMRMwEQYLKwYB\nBAGCNzwCAQMTAlVTMRkwFwYLKwYBBAGCNzwCAQITCERlbGF3YXJlMRAwDgYDVQQF\nEwc1MTU3NTUwMRcwFQYDVQQJEw41NDggNHRoIFN0cmVldDEOMAwGA1UEERMFOTQx\nMDcxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1T\nYW4gRnJhbmNpc2NvMRUwEwYDVQQKEwxHaXRIdWIsIEluYy4xEzARBgNVBAMTCmdp\ndGh1Yi5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCx1Nw8r/3z\nTu3BZ63myyLot+KrKPL33GJwCNEMr9YWaiGwNksXDTZjBK6/6iBRlWVm8r+5TaQM\nKev1FbHoNbNwEJTVG1m0Jg/Wg1dZneF8Cd3gE8pNb0Obzc+HOhWnhd1mg+2TDP4r\nbTgceYiQz61YGC1R0cKj8keMbzgJubjvTJMLy4OUh+rgo7XZe5trD0P5yu6ADSin\ndvEl9ME1PPZ0rd5qM4J73P1LdqfC7vJqv6kkpl/nLnwO28N0c/p+xtjPYOs2ViG2\nwYq4JIJNeCS66R2hiqeHvmYlab++O3JuT+DkhSUIsZGJuNZ0ZXabLE9iH6H6Or6c\nJL+fyrDFwGeNAgMBAAGjggHuMIIB6jAfBgNVHSMEGDAWgBQ901Cl1qCt7vNKYApl\n0yHU+PjWDzAdBgNVHQ4EFgQUakOQfTuYFHJSlTqqKApD+FF+06YwJQYDVR0RBB4w\nHIIKZ2l0aHViLmNvbYIOd3d3LmdpdGh1Yi5jb20wDgYDVR0PAQH/BAQDAgWgMB0G\nA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjB1BgNVHR8EbjBsMDSgMqAwhi5o\ndHRwOi8vY3JsMy5kaWdpY2VydC5jb20vc2hhMi1ldi1zZXJ2ZXItZzEuY3JsMDSg\nMqAwhi5odHRwOi8vY3JsNC5kaWdpY2VydC5jb20vc2hhMi1ldi1zZXJ2ZXItZzEu\nY3JsMEIGA1UdIAQ7MDkwNwYJYIZIAYb9bAIBMCowKAYIKwYBBQUHAgEWHGh0dHBz\nOi8vd3d3LmRpZ2ljZXJ0LmNvbS9DUFMwgYgGCCsGAQUFBwEBBHwwejAkBggrBgEF\nBQcwAYYYaHR0cDovL29jc3AuZGlnaWNlcnQuY29tMFIGCCsGAQUFBzAChkZodHRw\nOi8vY2FjZXJ0cy5kaWdpY2VydC5jb20vRGlnaUNlcnRTSEEyRXh0ZW5kZWRWYWxp\nZGF0aW9uU2VydmVyQ0EuY3J0MAwGA1UdEwEB/wQCMAAwDQYJKoZIhvcNAQELBQAD\nggEBAG/nbcuC8++QhwnXDxUiLIz+06scipbbXRJd0XjAMbD/RciJ9wiYUhcfTEsg\nZGpt21DXEL5+q/4vgNipSlhBaYFyGQiDm5IQTmIte0ZwQ26jUxMf4pOmI1v3kj43\nFHU7uUskQS6lPUgND5nqHkKXxv6V2qtHmssrA9YNQMEK93ga2rWDpK21mUkgLviT\nPB5sPdE7IzprOCp+Ynpf3RcFddAkXb6NqJoQRPrStMrv19C1dqUmJRwIQdhkkqev\nff6IQDlhC8BIMKmCNK33cEYDfDWROtW7JNgBvBTwww8jO1gyug8SbGZ6bZ3k8OV8\nXX4C2NesiZcLYbc2n7B9O+63M2k=\n-----END CERTIFICATE-----".getBytes(Charsets.US_ASCII)
      val actual = certificateParser.parse(githubCert)
      val expected = Certificate(
        BigInt("15953718796281471505685363726901697671"),
        Some("github.com"),
        "BusinessCategory=Private Organization,1.3.6.1.4.1.311.60.2.1.3=US,1.3.6.1.4.1.311.60.2.1.2=Delaware,SERIALNUMBER=5157550,STREET=548 4th Street,PostalCode=94107,C=US,ST=California,L=San Francisco,O=GitHub\\, Inc.,CN=github.com",
        Some(List(List(2, "github.com"), List(2, "www.github.com"))),
        new DateTime(2016,4,12,12,0,0, DateTimeZone.UTC),
        "C=US,O=DigiCert Inc,OU=www.digicert.com,CN=DigiCert SHA2 Extended Validation Server CA")
      expected === actual
    }
  }

}
