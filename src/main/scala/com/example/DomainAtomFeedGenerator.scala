package com.example

class DomainAtomFeedGenerator {
  def generateAtomFeed(domain: String, certificates: Seq[LogEntry]) = {
    <feed xmlns="http://www.w3.org/2005/Atom"
          xmlns:dc="http://purl.org/dc/elements/1.1/">
          
      <author>
        <name>Certificate Transparency Watch</name>
      </author>
      <id>http://ct-watch.tom-fitzhenry.me.uk/domain/{domain}</id>
      <title>Certificates for {domain}</title>

      {
        for (entry <- certificates) yield {
          <entry>
            <content>-----BEGIN CERTIFICATE-----{entry.certificate}-----END CERTIFICATE-----</content>
            <title>{entry.domain}</title>
          </entry>
        }
      }
    </feed>
  }
}