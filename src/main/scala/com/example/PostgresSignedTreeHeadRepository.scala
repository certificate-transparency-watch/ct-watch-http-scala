package com.example

import net.noerd.prequel.DatabaseConfig
import net.noerd.prequel.SQLFormatterImplicits._
import net.noerd.prequel.ResultSetRowImplicits._
import scalaz.std.boolean._

class PostgresSignedTreeHeadRepository(database: DatabaseConfig) extends SignedTreeHeadRepository {
  
  val findByLogServerName: String => Option[Seq[SignedTreeHead]] = { logServerName: String =>
    option(exists(logServerName), database.transaction { tx =>
        tx.select("SELECT sth.* FROM sth JOIN log_server ON sth.log_server_id = log_server.id WHERE log_server.name = ?", logServerName) { r => SignedTreeHead(r, r, r, r, r, r) }
    })
  }
  
  
  
  private def exists(logServerName: String): Boolean = {
    database.transaction { tx =>
      tx.selectBoolean("select exists(select * from log_server where name = ?)", logServerName)
    }
  }

}