package com.example

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTime

class DomainAtomFeedGenerator {
  def generateAtomFeed(domain: String, certificates: Seq[LogEntry]) = {
    val now = ISODateTimeFormat.dateTime().print(new DateTime())
    
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
          
          <entry>
            <id>{str}</id>
            <link href={ '"' + str + '"' }/>
            <content>-----BEGIN CERTIFICATE-----{entry.certificate}-----END CERTIFICATE-----</content>
            <title>{entry.domain}</title>
            <updated>{now}</updated>
            <dc:date>{now}</dc:date>
          </entry>
        }
      }
    </feed>
  }
}