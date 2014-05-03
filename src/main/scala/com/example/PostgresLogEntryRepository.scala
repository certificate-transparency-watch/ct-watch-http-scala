package com.example

import net.noerd.prequel.DatabaseConfig
import net.noerd.prequel.SQLFormatterImplicits._
import net.noerd.prequel.ResultSetRowImplicits._

class PostgresLogEntryRepository(db: DatabaseConfig) extends LogEntryRepository {
  def lookupByDomain(domain: String) = {
    db.transaction { tx =>
      tx.select("SELECT log_server.prefix, idx, domain, encode(certificate, 'base64') as certificate FROM log_entry JOIN log_server ON log_server.id = log_server_id JOIN cert ON log_entry.cert_md5 = cert.md5 WHERE reverse(domain) = ? OR reverse(domain) like (? || '.%%')", domain.reverse, domain.reverse) { r =>
        LogEntry(r, r, r, r)
      }
    }
  }
}

