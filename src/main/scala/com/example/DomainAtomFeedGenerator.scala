package com.example

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTimeZone, DateTime}

class DomainAtomFeedGenerator {
  def generateAtomFeed(domain: String, certificates: Seq[LogEntry]) = {
    val now = ISODateTimeFormat.dateTime().print(new DateTime(DateTimeZone.UTC))

    val parser = new CertificateParser
    
    <feed xmlns="http://www.w3.org/2005/Atom"
          xmlns:dc="http://purl.org/dc/elements/1.1/">
          
      <author>
        <name>Certificate Transparency Watch</name>
      </author>
      <id>http://ct-watch.tom-fitzhenry.me.uk/domain/{domain}</id>
      <title>Certificates for {domain}</title>
      <updated>{now}</updated>

      {
        for (entry <- certificates) yield {
          val str = s"https://${entry.logServer}/ct/v1/get-entries?start=${entry.idx}&end=${entry.idx}"
          val certificate = parser.parse(entry)
          val title = (certificate.commonName match {
            case Some(d) => d + " by "
            case None    => ""
          }) + (certificate.issuerDN + " with serial number " + certificate.serialNumber)
          <entry>
            <id>{str}</id>
            <link href={str}/>
            <title>{title}</title>
            <content type="xhtml">
              <h2>{title}</h2>
              <table>
                  <tr>
                    <td>Common Name</td>
                    <td>{certificate.commonName}</td>
                  </tr>
                  <tr>
                    <td>Subject</td>
                    <td>{certificate.subject}</td>
                  </tr>
                  <tr>
                    <td>Subject Alternative Names</td>
                    <td>{certificate.subjectAlternativeNames}</td>
                  </tr>
                  <tr>
                    <td>Expiration Date</td>
                    <td>{certificate.expiry}</td>
                  </tr>
                  <tr>
                    <td>Issuer</td>
                    <td>{certificate.issuerDN}</td>
                  </tr>
                  <tr>
                    <td>Serial Number</td>
                    <td>{certificate.serialNumber}</td>
                  </tr>
                  <tr>
                    <td>Log Server</td>
                    <td>https://{entry.logServer}</td>
                  </tr>
                  <tr>
                    <td>Log Server Index</td>
                    <td>{entry.idx}</td>
                  </tr>
                  <tr>
                    <td>Link to entry in log server</td>
                    <td><a href={str}>{str}</a></td>
                  </tr>
              </table>

              <h3>Raw certificate</h3>
              <em>Copy to cert.crt and parse with `openssl x509 -in cert.crt -text -noout`</em>
              <pre>
-----BEGIN CERTIFICATE-----
{entry.certificate}
-----END CERTIFICATE-----
              </pre>
            </content>

            <updated>{now}</updated>
            <dc:date>{now}</dc:date>
          </entry>
        }
      }
    </feed>
  }
}