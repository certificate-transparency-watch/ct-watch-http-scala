package com.example

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result
import com.codahale.metrics.health.HealthCheckRegistry
import net.noerd.prequel.DatabaseConfig
import org.joda.time.DateTime

class HealthChecks(healthCheckRegistry: HealthCheckRegistry, db: DatabaseConfig) {
  healthCheckRegistry.register("recent sth", new RecentSthCheck(db))
  healthCheckRegistry.register("unprocessed log entries", new UnprocessedLogEntriesCheck(db))
  healthCheckRegistry.register("unverified sth", new UnverifiedSthCheck(db))
}

class RecentSthCheck(db: DatabaseConfig) extends HealthCheck {
  override def check(): Result = {
    val results: Seq[DateTime] = db.transaction { tx =>
      tx.select("SELECT max(timestamp) FROM sth GROUP BY log_server_id") { r =>
        new DateTime(r.nextLong.get)
      }
    }

    if (results.length != 2) {
      Result.unhealthy("There exists a log server that has 0 STHs")
    } else {
      if (results.forall { datetime => datetime.isAfter(new DateTime().minusHours(3)) })
        Result.healthy()
      else
        Result.unhealthy("There exists a log server whose latest STH is >3 hours")
    }
  }

}

class UnprocessedLogEntriesCheck(db: DatabaseConfig) extends HealthCheck {
  override def check(): Result = {
    val numberOfUnprocessedLogEntries: Int = db.transaction { tx =>
      tx.selectInt("select count(*) from log_entry where domain is null")
    }

    if (numberOfUnprocessedLogEntries > 100000) {
      Result.unhealthy("There are " + numberOfUnprocessedLogEntries + " unprocessed log entries.")
    } else {
      Result.healthy()
    }
  }
}

class UnverifiedSthCheck(db: DatabaseConfig) extends HealthCheck {
  override def check(): Result = {
    val numberOfUnverifiedSths: Seq[Int] = db.transaction { tx =>
      tx.select("select count(*) from sth where verified = false group by log_server_id") { r =>
        r.nextInt.get
      }
    }

    if (numberOfUnverifiedSths.exists { _ > 1 })
      Result.unhealthy("There exists a log server with more than one unverified STH")
    else
      Result.healthy()
  }
}

class SthDrift extends HealthCheck {
  override def check(): Result = throw new NotImplementedError()
}

class MaximumMergeDelayCheck extends HealthCheck {
  override def check(): Result = throw new NotImplementedError()
}